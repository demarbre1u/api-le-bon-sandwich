package org.lpro.boundary;

import java.net.URI;
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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.lpro.entity.Commande;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("commandes")
public class CommandeRepresentation 
{
    @Inject
    CommandeRessource commandeRessource;
    @Context
    UriInfo uriInfo;

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

    @POST
    public Response addCommande(@Valid Commande commande)
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

        Commande newCommande = this.commandeRessource.save(commande);
        URI uri = uriInfo.getAbsolutePathBuilder().path(newCommande.getId()).build();
        return Response.created(uri)
                .entity(newCommande)
                .build();
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
}
