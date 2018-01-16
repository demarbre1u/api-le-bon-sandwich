package org.lpro.boundary;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.net.URI;
import java.security.Key;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.lpro.control.KeyManagement;
import org.lpro.entity.Carte;
import org.lpro.entity.Commande;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("commandes")
public class CommandeRepresentation 
{
    @Inject
    CommandeRessource commandeRessource;

    @Inject
    CarteManager cm;

    @Inject 
    KeyManagement keyManagement;

    @Context
    UriInfo uriInfo;

    /*********************************************************************
     * 
     * Route permettant de récupérer les informations d'une commande
     * 
     *********************************************************************/

    @GET
    @Path("/{commandeId}")
    public Response getOneCommande(@PathParam("commandeId") String commandeId, 
        @DefaultValue("") @QueryParam("token") String tokenParam,
        @DefaultValue("") @HeaderParam("X-lbs-token") String tokenHeader) 
    {
        Commande cmd = commandeRessource.findById(commandeId);

        if(cmd == null) 
            return Response.status(Response.Status.NOT_FOUND).build();

        if(tokenParam.isEmpty() && tokenHeader.isEmpty())
            return Response.status(Response.Status.FORBIDDEN).build();

        String token = (tokenParam.isEmpty()) ? tokenHeader : tokenParam;
        Boolean isTokenValid = cmd.getToken().equals(token);
        
        if(!isTokenValid)
            return Response.status(Response.Status.FORBIDDEN).build();
        else    
            return Response.ok(buildCommandeObject(cmd)).build();
    }

    private JsonObject buildCommandeObject(Commande c) 
    {
        return Json.createObjectBuilder()
                .add("commande", buildJsonForCommande(c))
                .build();
    }

    private JsonObject buildJsonForCommande(Commande c) 
    {
        return Json.createObjectBuilder()
                .add("id", c.getId())
                .add("nom_client", c.getNom())
                .add("mail_client", c.getMail())
                .add("livraison", buildJsonForLivraison(c))
                .add("token", c.getToken())
                .build();
    }

    private JsonObject buildJsonForLivraison(Commande c) 
    {
        return Json.createObjectBuilder()
                .add("date", c.getDateLivraison())
                .add("heure", c.getHeureLivraison())
                .build();
    }

    /*********************************************************************
     * 
     * Route permettant de créer une nouvelle commande
     * 
     *********************************************************************/

    @POST
    public Response addCommande(@Valid Commande commande, @DefaultValue("") @QueryParam("card") String cardId, @DefaultValue("") @HeaderParam("Authorization") String bearer)
    {
        String date = commande.getDateLivraison() + " " + commande.getHeureLivraison();
        TimeZone tz = TimeZone.getTimeZone("Europe/Paris");
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        format.setTimeZone(tz);
        
        Date cmdDate;

        try 
        {
			cmdDate = format.parse(date);
        } 
        catch (ParseException e) 
        {
            // Si le format de la date n'est pas valide
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        Date currentDate = new Date();

        if(cmdDate.compareTo(currentDate) <= 0)
        {
            // Si la date précisée précède la date courante 
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // Si il y a un token bearer
        if(!bearer.isEmpty() && bearer.startsWith("Bearer "))
        {
            // On le parse
            String token = bearer.substring("Bearer".length()).trim();
            
            try
            {
                Key key = keyManagement.generateKey();
                Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
                String tokenCardId = (String) claims.getBody().get("carte");

                if(tokenCardId.equals(cardId))
                {
                    Carte c = cm.findById(tokenCardId);
                    commande.setCarte(c);
                }
            }
            catch(Exception e) {}
        }
            

        Commande newCommande = this.commandeRessource.save(commande);
        URI uri = uriInfo.getAbsolutePathBuilder().path(newCommande.getId()).build();
        return Response.created(uri)
                .entity(newCommande)
                .build();
    }

    /*********************************************************************
     * 
     * Route permettant de modifier la date de livraison d'une commande
     * 
     *********************************************************************/

    @PUT
    @Path("/{id}")
    public Response changeCommandeDate(@PathParam("id") String uid, @DefaultValue("") @HeaderParam("date") String date, @DefaultValue("") @HeaderParam("heure") String heure)
    {
        Commande commande = commandeRessource.findById(uid);
        if(commande == null) return Response.status(Response.Status.BAD_REQUEST).build();

        TimeZone tz = TimeZone.getTimeZone("Europe/Paris");
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        format.setTimeZone(tz);

        String newDateRaw = date + " " + heure;
        
        Date newDateParsed;

        try 
        {
            newDateParsed = format.parse(newDateRaw);
        } 
        catch (ParseException e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        Date currentDate = new Date();

        if(newDateParsed.compareTo(currentDate) <= 0)
        {
            // Si la date précisée précède la date courante 
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        commande.setDateLivraison(date);
        commande.setHeureLivraison(heure);
        
        URI uri = uriInfo.getAbsolutePathBuilder().build();

        return Response.ok().header("Location", uri).build();
    }
}
