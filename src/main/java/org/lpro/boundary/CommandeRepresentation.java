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
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
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
import org.lpro.entity.Sandwich;
import org.lpro.entity.Tailles;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("commandes")
public class CommandeRepresentation 
{
    @Inject
    CommandeRessource commandeRessource;

    @Inject 
    SandwichManager sm;

    @Inject
    TailleManager tm;

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
    @Path("{id}")
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

        return Response.ok().location(uri).build();
    }

    /*********************************************************************
     * 
     * Route permettant de payer une commande
     * 
     *********************************************************************/

    @POST
    @Path("{id}")
    public Response payerCommande(@PathParam("id") String uid,
        @DefaultValue("") @HeaderParam("numCarte") String numCarte,
        @DefaultValue("") @HeaderParam("dateExpiration") String dateExpiration)
    {
        Commande commande = commandeRessource.findById(uid);
        if(commande == null)
        {
            JsonObject json = Json.createObjectBuilder()
                .add("error", "The specified UID doesn't exist")
                .build();

            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }

        if(commande.isPayed())
        {
            JsonObject json = Json.createObjectBuilder()
                .add("error", "This command has already been payed")
                .build();

            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }

        Pattern patternNumCarte = Pattern.compile("\\d{16}");
        Pattern patternDateExpi = Pattern.compile("\\d{2}/\\d{2}");
        
        Matcher matcherNumCarte = patternNumCarte.matcher(numCarte);
        Matcher matcherDateExpi = patternDateExpi.matcher(dateExpiration);
        
        if(matcherNumCarte.find() && matcherDateExpi.find())
        {
            commande.setPayed(true);

            URI uri = uriInfo.getAbsolutePathBuilder().build();
            
            return Response.ok().location(uri).build();        
        }
        else
        {
            JsonObject json = Json.createObjectBuilder()
                .add("error", "numCarte or dateExpiration isn't valid")
                .build();

            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        }
    }

    /*********************************************************************
     * 
     * Route permettant d'ajouter un sandwich à une commande
     * 
     *********************************************************************/

     @POST
     @Path("{uid}/sandwich")
     public Response addSandwich(@PathParam("uid") String uidCommande,
         @DefaultValue("") @QueryParam("uidSandwich") String uidSandwich, 
         @QueryParam("uidTaille") long uidTaille,
         @QueryParam("nbSandwich") int nbSandwich) 
     {
        // On vérifie que les uid sont bons
        boolean valide = !uidSandwich.isEmpty() && uidTaille > 0 && nbSandwich > 0;
        if(!valide)
            return Response.status(Response.Status.BAD_REQUEST).build();
        
        // On vérifie que les uid correspondent à quelque chose qui existe
        Commande commande = commandeRessource.findById(uidCommande);
        Sandwich sandwich = sm.findById(uidSandwich);
        Tailles taille = tm.findById(uidTaille);

        if(commande == null || sandwich == null || taille == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        
        // On ajoute la taille voulu au sandwich
        sandwich.getTailles().add(taille);

        // On ajoute le sandwich a la commande
        for(int i = 0 ; i < nbSandwich ; i++)
        {
            commande.getSandwich().add(sandwich);
        }

        URI uri = uriInfo.getBaseUriBuilder().path("/" + uidCommande).build();

        return Response.ok().location(uri).build();
     }

     /*********************************************************************
     * 
     * Route permettant de récupérer la liste des commandes (private)
     * 
     *********************************************************************/

    @GET
    @Path("private")
    public Response getListCommandes() 
    {
        JsonObject json = Json.createObjectBuilder()
            .add("type", "collection")
            .add("commandes", buildCommandes())
            .build();

        return Response.ok(json).build();
    }

    private JsonValue buildCommandes() 
    {
        JsonArrayBuilder jab = Json.createArrayBuilder();

        List<Commande> commandes = commandeRessource.findAll();

        for(Commande c : commandes)
        {
            JsonObject json = Json.createObjectBuilder()
                .add("id", c.getId())
                .add("dateLivraison", c.getDateLivraison())
                .add("heureLivraison", c.getHeureLivraison())
                .add("links", buildLink(c))
                .build();
            
            jab.add(json);
        }

		return jab.build();
    }
    
    private JsonValue buildLink(Commande c)
    {
        URI uri = uriInfo.getBaseUriBuilder().path("/commandes/" + c.getId() + "/private").build();

        JsonObject json = Json.createObjectBuilder()
            .add("self", uri.toString())
            .build();

        return json;
    }

    /*********************************************************************
     * 
     * Route permettant de récupérer les détails d'une commande (private)
     * 
     *********************************************************************/

    @GET
    @Path("{uid}/private")
    public Response getDetailsCommande(@PathParam("uid") String uid)
    {
        // On vérifie que uid correspond à une commande existante
        Commande commande = commandeRessource.findById(uid);

        if(commande == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        // On construit le json
        JsonObject json = Json.createObjectBuilder()
            .add("type", "ressource")
            .add("commande", jsonCommande(commande))
            .build();

        return Response.ok(json).build();
    }

    private JsonValue jsonCommande(Commande commande) 
    {
        JsonObject json = Json.createObjectBuilder()
        .add("id", commande.getId())
        .add("nom", commande.getNom())
        .add("mail", commande.getMail())
        .add("date", commande.getDateLivraison())
        .add("heure", commande.getHeureLivraison())
        .add("payé", commande.isPayed())
        .add("sandwichs", jsonListSandwich(commande))
        .add("links", buildLink(commande))
        .build();

		return json;
	}

	private JsonValue jsonListSandwich(Commande commande) 
    {
        List<Sandwich> sandwichs = commande.getSandwich();

        JsonArrayBuilder jab = Json.createArrayBuilder();

        for(Sandwich s : sandwichs)
        {
            JsonObject json = Json.createObjectBuilder()
                .add("id", s.getId())
                .add("nom", s.getNom())
                .add("description", s.getDescription())
                .add("type", s.getType())
                .add("tailles", jsonListTailles(s))
                .build();
            
            jab.add(json);
        }

		return jab.build();
    }
    
    private JsonValue jsonListTailles(Sandwich s)
    {
        List<Tailles> tailles = s.getTailles();

        JsonArrayBuilder jab = Json.createArrayBuilder();

        for(Tailles t : tailles)
        {
            JsonObject json = Json.createObjectBuilder()
                .add("nom", t.getNom())
                .add("prix", t.getPrix())
                .build();

            jab.add(json);
        }

        return jab.build();
    }

    /*********************************************************************
     * 
     * Route permettant de changer l'état d'une commande (private)
     * 
     *********************************************************************/

     @PUT
     @Path("{uid}/private")
     public Response changeCommandeState(@PathParam("uid") String uid) 
     {
        Commande commande = commandeRessource.findById(uid);

        if(commande.getSandwich() == null || commande.getSandwich().isEmpty() || commande.isPayed())
            return Response.status(Response.Status.BAD_REQUEST).build();
     
        commande.setPayed(true);
        URI uri = uriInfo.getBaseUriBuilder().path("/commandes/" + uid + "/private").build();

        return Response.ok().location(uri).build();
    }

}
