package org.lpro.boundary;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.lpro.entity.Sandwich;

@Stateless
public class SandwichManager
{
    @PersistenceContext
    EntityManager em;

    public List<Sandwich> findAll(String ptype, String img, int page, int size)
    {
        // On crée une requête avec des critères
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery cq = qb.createQuery();
        Root<Sandwich> sandwich = cq.from(Sandwich.class);

        List<Predicate> predicates = new ArrayList<Predicate>();

        // Si le type de pain est spécifié, on l'ajoute en critère
        if(ptype != null)
            predicates.add(qb.equal(sandwich.get("type_pain"), ptype));

        // Si img n'est pas null, on l'ajoute comme critère
        if(img != null)
            predicates.add(qb.isNotNull(sandwich.get("img")));

        // On finit de construire la requête
        cq.select(sandwich).where(predicates.toArray(new Predicate[]{}));

        Query q = em.createQuery(cq);
        q.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH);
        q.setFirstResult( (page - 1) * size );
        q.setMaxResults(size); 

        return q.getResultList();
    }

    public Sandwich findById(long id)
    {
        return em.find(Sandwich.class, id);
    }

    public long count()
    {
        Query nb = em.createQuery("SELECT count(s.id) from Sandwich s");
        
        return (long) nb.getSingleResult();
    }

    public Sandwich save(Sandwich s) 
    {
		return this.em.merge(s);
	}

    public void delete(long id) 
    {
        try
        {
            Sandwich ref = em.getReference(Sandwich.class, id);
            this.em.remove(ref);
        }
        catch(EntityNotFoundException e) { /* Rien à faire */ }
    }
}