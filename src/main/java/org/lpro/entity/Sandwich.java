package org.lpro.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQuery(name="Sandwich.findAll",query="SELECT s FROM Sandwich s")
public class Sandwich implements Serializable
{
    @Id
    private long id;
    @NotNull
    private String nom;
    @NotNull
    private String description;
    @NotNull
    private String type_pain;
    private String img;

    public Sandwich() {}

    public Sandwich(long id, String nom, String desc, String type, String img)
    {
        this.id = id;
        this.nom = nom;
        this.description = desc;
        this.type_pain = type;
        this.img = img;
    }
    
    public long getId() 
    {
        return id;
    }

    public String getDescription() 
    {
        return description;
    }

    public String getImg() 
    {
        return img;
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

    public void setId(long id) 
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
}