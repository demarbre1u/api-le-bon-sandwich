package org.lpro.boundary;

import java.net.URI;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.lpro.control.PasswordManagement;
import org.lpro.entity.Utilisateurs;


@Stateless
@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UtilisateurResource
{
    @Inject
    UtilisateurManager um;

    @Context
    UriInfo uriInfo;

    /*********************************************************************
     * 
     * Route permettant de créer un nouvel utilisateur
     * 
     *********************************************************************/
    
     @POST
     public Response newUser(@Valid Utilisateurs u)
     {
        // On génère puis on change le mdp avant de le save dans la BDD
        u.setPassword(PasswordManagement.digestPassword(u.getPassword()));

        Utilisateurs newUser = um.save(u);
        URI uri = uriInfo.getAbsolutePathBuilder().path("/").build();

        return Response.created(uri).build();
     }
}