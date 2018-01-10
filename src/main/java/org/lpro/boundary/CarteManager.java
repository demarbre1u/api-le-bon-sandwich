package org.lpro.boundary;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.lpro.entity.Carte;

@Stateless
@Transactional
public class CarteManager
{
    @PersistenceContext
    EntityManager em;

    public Carte findById(String uid)
    {
        return this.em.find(Carte.class, uid);
    }

    public Carte save(Carte c)
    {
        return this.em.merge(c);
    }
}