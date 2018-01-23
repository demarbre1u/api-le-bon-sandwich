package org.lpro.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.JsonObject;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Pour se connecter à la base de données PgSQL
 * 
 * sudo docker run -v $PWD:/docker-entrypoint-initdb.d -it --rm --net=dockerlpro_lpronet lpro/pg11 psql -h db -U td1
 */

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQuery(name="Sandwich.findAll",query="SELECT s FROM Sandwich s")
public class Sandwich implements Serializable
{
    @Id
    private String id;
    @NotNull
    private String nom;
    @NotNull
    private String description;
    @NotNull
    private String type_pain;
    private String img;
    @ManyToMany(mappedBy="sandwich")
    private Set<Categorie> categorie = new HashSet<Categorie>();
    @ManyToMany(mappedBy="sandwich")
    private List<Tailles> tailles;
    @ManyToMany(mappedBy="sandwich")
    private List<Commande> commandes;

    public Sandwich() {}

    public Sandwich(String id, String nom, String desc, String type, String img)
    {
        this.id = id;
        this.nom = nom;
        this.description = desc;
        this.type_pain = type;
        this.img = img;
        this.categorie = new HashSet<>();
    }
    
    public List<Tailles> getTailles() 
    {
        return tailles;
    }

    public void setTailles(List<Tailles> tailles) 
    {
        this.tailles = tailles;
    }

    public String getId() 
    {
        return id;
    }

    public String getDescription() 
    {
        return description;
    }

    public String getImg() 
    {
        return img == null ? JsonObject.NULL.toString() : img;
    }

    public String getNom() 
    {
        return nom;
    }

    public String getType() 
    {
        return type_pain;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public void setId(String id) 
    {
        this.id = id;
    }

    public void setImg(String img) 
    {
        this.img = img;
    }

    public void setNom(String nom) 
    {
        this.nom = nom;
    }

    public void setType(String type) 
    {
        this.type_pain = type;
    }

    public Set<Categorie> getCategorie() 
    {
        return categorie;
    }

    public void setCategorie(Set<Categorie> categorie) 
    {
        this.categorie = categorie;
    }
}