package org.lpro.boundary;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.lpro.entity.Utilisateurs;

@Stateless
@Transactional
public class UtilisateurManager
{
    @PersistenceContext
    EntityManager em;

    public Utilisateurs findByUsername(String uname)
    {
        return this.em.find(Utilisateurs.class, uname);
    }

    public Utilisateurs save(Utilisateurs u)
    {
        return this.em.merge(u);
    } 
}