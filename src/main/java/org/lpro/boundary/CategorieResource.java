package org.lpro.boundary;

import java.net.URI;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.lpro.entity.Categorie;
import org.lpro.entity.Sandwich;


@Stateless
@Path("categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategorieResource 
{    
    @Inject
    CategorieManager cm;

    @Inject
    SandwichManager sm;

    @Context 
    UriInfo uriInfo;
    
    @GET
    public Response getCategories() 
    {
        JsonObject json = Json.createObjectBuilder()
                .add("type", "collection")
                .add("categories", getCategorieList())
                .build();
        return Response.ok(json).build();
    }
    
    @GET
    @Path("{id}")
    public Response getOneCategorie(@PathParam("id") long id) 
    {
        return Optional.ofNullable(cm.findById(id))
                .map(c -> Response.ok(buildCategorieObject(c)).build())
                .orElseThrow(() -> new CategorieNotFound("Ressource non disponible "+ uriInfo.getPath()));
    }

    private JsonObject buildCategorieObject(Categorie c) 
    {
        return Json.createObjectBuilder()
            .add("categorie", buildJsonForCategorie(c))
            .build();
	}

    private JsonValue buildJsonForCategorie(Categorie c) 
    {
        String uriSelf = uriInfo.getBaseUriBuilder()
            .path(CategorieResource.class)
            .path(c.getId() + "")
            .build()
            .toString();

        String uriSandwichs = uriInfo.getBaseUriBuilder()
            .path(CategorieResource.class)
            .path(c.getId() + "")
            .path(SandwichResource.class)
            .build()
            .toString();

        JsonArrayBuilder links = Json.createArrayBuilder();
        links.add(buildJsonSelfUri(uriSelf));
        links.add(buildJsonSandwichUri(uriSandwichs));

        JsonArrayBuilder sandwichs = Json.createArrayBuilder();
        c.getSandwich().forEach( s ->
        {
            sandwichs.add(buildJsonForSandwich(s));
        });

        return Json.createObjectBuilder()
            .add("id", c.getId())
            .add("nom", c.getNom())
            .add("desc", c.getDescription())
            .add("sandwichs", sandwichs.build())
            .build();
	}

    private JsonValue buildJsonSandwichUri(String uriSandwichs) 
    {
        return Json.createObjectBuilder()
            .add("href", uriSandwichs)
            .add("rel", "sandwichs")
            .build();
	}

	private JsonValue buildJsonSelfUri(String uriSelf) {
        return Json.createObjectBuilder()
            .add("href", uriSelf)
            .add("rel", "self")
            .build();
	}

	@GET
    @Path("{id}/sandwichs")
    public Response getSandwichByCategory(@PathParam("id") long id)
    {
        return Optional.ofNullable(cm.findById(id))
            .map(c -> Response.ok(buildSandwichToCategory(c)).build())
            .orElseThrow(() -> new CategorieNotFound("Ressource non disponible "+ uriInfo.getPath()));
    }

	@POST
    public Response newCategorie(@Valid Categorie c)
    {
        Categorie newOne = cm.save(c);
        long id = newOne.getId();
        URI uri = uriInfo.getAbsolutePathBuilder().path("/" + id).build();

        return Response.created(uri).build();
    }

    @DELETE
    @Path("{id}")
    public Response suppression(@PathParam("id") long id)
    {
        cm.delete(id);

        return Response.status(Response.Status.NO_CONTENT).build();
    }  

    @PUT
    @Path("{id}")
    public Categorie update(@PathParam("id") long id, Categorie c)
    {
        c.setId(id);

        return cm.save(c);
    } 

    private JsonObject buildSandwichToCategory(Categorie c) 
    {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        c.getSandwich().forEach( s -> 
        {
            jab.add(buildJsonForSandwich(s));
        });
        
        return Json.createObjectBuilder()
                .add("sandwichs", jab.build())
                .build();
	}
    
    private JsonValue buildJsonForSandwich(Sandwich s) 
    {
        String uriSandwich = uriInfo.getBaseUriBuilder()
            .path(SandwichResource.class)
            .path(s.getId() + "")
            .build()
            .toString();
        JsonObject job = Json.createObjectBuilder()
            .add("href", uriSandwich)
            .add("rel", "self")
            .build();
        JsonArrayBuilder links = Json.createArrayBuilder();
        links.add(job);
        
        return Json.createObjectBuilder()
            .add("id", s.getId())
            .add("nom", s.getNom())
            .add("desc", s.getDescription())
            .add("type_pain", s.getType())
            .add("img", s.getImg())
            .add("links", links)
            .build();
	}

	private JsonArray getCategorieList() 
    {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        this.cm.findAll().forEach((c) -> {
            jab.add(buildJson(c));
        });
        return jab.build();
    }
    
    private JsonObject buildJson(Categorie c) 
    {
        return Json.createObjectBuilder()
                .add("id",c.getId())
                .add("nom", c.getNom())
                .add("desc", c.getDescription())
                .build();
    }
}
