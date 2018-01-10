package org.lpro.boundary;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.lpro.control.KeyManagement;
import org.lpro.control.RandomToken;
import org.lpro.entity.Carte;
import org.lpro.entity.Utilisateurs;
import org.lpro.provider.Secured;
import org.mindrot.jbcrypt.BCrypt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.net.URI;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Path("cartes")
public class CarteResource 
{
    @Inject
    private KeyManagement keyManagement;

    @Inject
    UtilisateurManager um;

    @Inject
    CarteManager cm;

    @Context
    private UriInfo uriInfo;

    /*********************************************************************
     * 
     * Route permettant de récupérer les informations d'une carte
     * 
     *********************************************************************/

    @GET
    @Secured
    @Path("{id}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getOneCarte(@PathParam("id") String uid, @HeaderParam("Authorization") String bearer)
    {
        String token = bearer.substring("Bearer".length()).trim();

        try
        {
            Key key = keyManagement.generateKey();
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            String carteUid = (String) claims.getBody().get("carte");

            if(!carteUid.equals(uid))
                throw new NotAuthorizedException("Problème d'authentification");
            
            Carte carte = cm.findById(uid);

            JsonObject json = Json.createObjectBuilder()
                .add("uid", carteUid)
                .add("montant", carte.getMontant())
                .add("reduction", carte.getReduction())
                .build();
            
            return Response.ok(json).build();
        }
        catch(Exception e)
        {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /*********************************************************************
     * 
     * Route permettant de créer une nouvelle carte
     * 
     *********************************************************************/

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response createNewCarte()
    {
        Carte newOne = new Carte();
        
        RandomToken rt = new RandomToken();
        newOne.setUid(rt.randomString(10));
        
        cm.save(newOne);
        String uid = newOne.getUid();
        URI uri = uriInfo.getAbsolutePathBuilder().path("/" + uid).build();

        return Response.created(uri).build();
    }

    /*********************************************************************
     * 
     * Route permettant de récupérer un token correspondant à une carte donnée
     * 
     *********************************************************************/

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("{id}/auth")
    public Response authenticateUser(@HeaderParam("Authorization") String session, @PathParam("id") String id)
    {
        if(session == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();        
        
        try 
        {
            String encodedInfo = session.substring("Basic".length()).trim();
            String decodedInfo = new String(Base64.getDecoder().decode(encodedInfo));
            String[] userInfo = decodedInfo.split(":");
            
            String nom = userInfo[0];
            String pwd = userInfo[1];
    
            tryAuthentication(nom, pwd);    

            String token = issueToken(nom, id);
            
            JsonObject json = Json.createObjectBuilder()
                .add("token", token)
                .build();

            return Response.ok(json).build();            
        }
        catch(Exception e)
        {
            return Response.status(Response.Status.UNAUTHORIZED).build();        
        }
    }

    private void tryAuthentication(String nom, String pwd) throws Exception
    {
        Utilisateurs user = um.findByUsername(nom);

        if(user == null || !BCrypt.checkpw(pwd, user.getPassword()))
        {
            throw new NotAuthorizedException("Problème d'authentification");
        }
    }
    
    private String issueToken(String nom, String id) 
    {
        Key key = keyManagement.generateKey();
        String jwtToken = Jwts.builder()
            .setSubject(nom)
            .setIssuer(uriInfo.getAbsolutePath().toString())
            .setIssuedAt(new Date())
            .setExpiration(toDate(LocalDateTime.now().plusMinutes((5))))
            .signWith(SignatureAlgorithm.HS512, key)
            .claim("carte", id)
            .compact();
            
        System.out.println(">>>> token/key : " + jwtToken + " -- " + key);
        
        return jwtToken;
	}

    private Date toDate(LocalDateTime localDateTime) 
    {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}
}