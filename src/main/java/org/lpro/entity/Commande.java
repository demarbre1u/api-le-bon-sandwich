package org.lpro.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
public class Commande implements Serializable 
{    
    @Id
    private String id;
    @NotNull
    private String nom;
    @Pattern(regexp="(?:[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
    private String mail;
    private String dateLivraison;
    private String heureLivraison;
    private String token;
    private boolean payed;
    
    @ManyToOne(fetch=FetchType.LAZY)
    private Carte carte;

    public Commande() {}

    public Commande(String nom, String mail, String dateLivraison, String heureLivraison) 
    {
        this.nom = nom;
        this.mail = mail;
        this.dateLivraison = dateLivraison;
        this.heureLivraison = heureLivraison;
        this.payed = false;
    }

    public String getId() 
    {
        return id;
    }

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getNom() 
    {
        return nom;
    }

    public Carte getCarte() 
    {
        return carte;
    }

    public boolean isPayed() 
    {
        return payed;
    }

    public void setPayed(boolean payed) 
    {
        this.payed = payed;
    }

    public void setCarte(Carte carte) 
    {
        this.carte = carte;
    }

    public void setNom(String nom) 
    {
        this.nom = nom;
    }

    public String getMail() 
    {
        return mail;
    }

    public void setMail(String mail) 
    {
        this.mail = mail;
    }

    public String getDateLivraison() 
    {
        return dateLivraison;
    }

    public void setDateLivraison(String dateLivraison) 
    {
        this.dateLivraison = dateLivraison;
    }

    public String getHeureLivraison() 
    {
        return heureLivraison;
    }

    public void setHeureLivraison(String heureLivraison) 
    {
        this.heureLivraison = heureLivraison;
    }

    public String getToken() 
    {
        return token;
    }

    public void setToken(String token) 
    {
        this.token = token;
    }
}



