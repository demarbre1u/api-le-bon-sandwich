package org.lpro.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Carte implements Serializable
{
    @Id
    private String uid;
    private float montant;
    private float reduction;

    @OneToMany(mappedBy="carte")
    private List<Commande> commandes;

    public Carte() {}

    public Carte(String uid, float montant, float reduction) 
    {
        this.uid = uid;
        this.montant = 0;
        this.reduction = 0;

        this.commandes = new ArrayList<Commande>();
    }

    public float getMontant() 
    {
        return montant;
    }

    public float getReduction() 
    {
        return reduction;
    }

    public String getUid() 
    {
        return uid;
    }

    public List<Commande> getCommandes() 
    {
        return commandes;
    }

    public void setCommandes(List<Commande> commandes) 
    {
        this.commandes = commandes;
    }

    public void setMontant(float montant) 
    {
        this.montant = montant;
    }

    public void setReduction(float reduction) 
    {
        this.reduction = reduction;
    }

    public void setUid(String uid)
     {
        this.uid = uid;
    }
}