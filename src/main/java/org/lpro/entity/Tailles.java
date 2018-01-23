package org.lpro.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

@Entity
@NamedQuery(name="Tailles.findAll",query="SELECT t FROM Tailles t")
public class Tailles implements Serializable
{
    @Id
    @GeneratedValue
    private long id;
    @NotNull
    private String nom;
    @NotNull
    private float prix;
    @ManyToMany
    private List<Sandwich> sandwich;

    public Tailles() {}

    public Tailles(long id, String nom, float prix)
    {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
    }

    public List<Sandwich> getSandwichs() 
    {
        return sandwich;
    }

    public void setSandwichs(List<Sandwich> sandwichs) 
    {
        this.sandwich = sandwichs;
    }

    public long getId() 
    {
        return id;
    }
   
    public String getNom() 
    {
        return nom;
    }

    public float getPrix() 
    {
        return prix;
    }

    public void setId(long id) 
    {
        this.id = id;
    }

    public void setNom(String nom) 
    {
        this.nom = nom;
    }

    public void setPrix(float prix) 
    {
        this.prix = prix;
    }
}