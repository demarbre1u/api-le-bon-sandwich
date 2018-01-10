package org.lpro.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
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

    public Carte() {}

    public Carte(String uid, float montant, float reduction) 
    {
        this.uid = uid;
        this.montant = 0;
        this.reduction = 0;
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