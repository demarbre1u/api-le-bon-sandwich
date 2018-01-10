package org.lpro.boundary;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
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
import org.lpro.control.PasswordManagement;
import org.lpro.entity.Utilisateurs;
import org.mindrot.jbcrypt.BCrypt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

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

    @Context
    private UriInfo uriInfo;

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("{id}/auth")
    public Response authenticateUser(@HeaderParam("Authorization") String session, @PathParam("id") long id)
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
            

            return Response.ok().header(AUTHORIZATION, "Bearer " + token).build();            
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
    
    private String issueToken(String nom, long id) 
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