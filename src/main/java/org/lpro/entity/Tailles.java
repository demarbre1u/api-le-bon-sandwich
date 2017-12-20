package org.lpro.entity;

import java.io.Serializable;
import java.util.HashSet;
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
    private Set<Sandwich> sandwich = new HashSet<Sandwich>();

    public Tailles() {}

    public Tailles(long id, String nom, float prix)
    {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.sandwich = new HashSet<>();
    }

    public Set<Sandwich> getSandwichs() 
    {
        return sandwich;
    }

    public void setSandwichs(Set<Sandwich> sandwichs) 
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