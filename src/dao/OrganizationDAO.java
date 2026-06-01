package dao;

import javax.persistence.*;
import java.util.List;
import models.Organization;

public class OrganizationDAO {
    private EntityManagerFactory emf;

    public OrganizationDAO() {
        this.emf = Persistence.createEntityManagerFactory("GHADSPU");
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public boolean isOrganizationNameExists(String name) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(o) FROM Organization o WHERE LOWER(o.name) = LOWER(:name)", Long.class)
                .setParameter("name", name.trim())
                .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public List<Organization> searchOrganizations(String keyword) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Organization> query = em.createQuery(
                "SELECT o FROM Organization o WHERE LOWER(o.name) LIKE LOWER(:kw) OR LOWER(o.type) LIKE LOWER(:kw)", Organization.class);
            query.setParameter("kw", "%" + keyword.trim() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean addOrganization(Organization org) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(org);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean updateOrganization(Organization org) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(org);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean deleteOrganization(int orgId) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Organization org = em.find(Organization.class, orgId);
            if (org != null) {
                em.remove(org);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public List<Organization> getAllOrganizations() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT o FROM Organization o", Organization.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public long countTotalOrganizations() {
        EntityManager em = getEntityManager();
        try { 
            return em.createQuery("SELECT COUNT(o) FROM Organization o", Long.class)
                     .getSingleResult(); 
        } finally { 
            em.close(); 
        }
    }
}