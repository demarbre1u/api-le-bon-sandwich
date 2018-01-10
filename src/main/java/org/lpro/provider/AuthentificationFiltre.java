package org.lpro.provider;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.lpro.control.KeyManagement;

import io.jsonwebtoken.Jwts;
import java.security.Key;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthentificationFiltre implements ContainerRequestFilter
{
    @Inject 
    private KeyManagement keyManagement;

    @Override 
    public void filter(ContainerRequestContext requestContext)
    {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if(authHeader == null || !authHeader.startsWith("Bearer "))
        {
            throw new NotAuthorizedException("Probleme header autorisation");
        } 

        String token = authHeader.substring("Bearer".length()).trim();

        try
        {
            Key key = keyManagement.generateKey();
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            System.out.println(">>>> token valide : " + token);
        }
        catch(Exception e)
        {
            System.out.println(">>>> token invalide : " + token);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}