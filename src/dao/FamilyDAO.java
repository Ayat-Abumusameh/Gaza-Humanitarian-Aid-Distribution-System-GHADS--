package dao;

import java.time.LocalDate;
import javax.persistence.*;
import java.util.List;
import java.util.Date;
import models.Family;

public class FamilyDAO {
    private EntityManagerFactory emf;

    public FamilyDAO() {
        this.emf = Persistence.createEntityManagerFactory("GHADSPU");
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public boolean isNationalIdExists(String nationalId, Long currentFamilyId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT COUNT(f) FROM Family f WHERE f.nationalId = :nid AND f.familyId != :fid";
            Long count = em.createQuery(jpql, Long.class)
                           .setParameter("nid", nationalId)
                           .setParameter("fid", currentFamilyId)
                           .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public boolean hasReceivedAidInLast30Days(int familyId) {
        EntityManager em = getEntityManager();
        try {
            LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

            Long count = em.createQuery(
                "SELECT COUNT(d) FROM AidDistribution d WHERE d.family.familyId = :famId AND d.distributionDate >= :cutoffDate", Long.class)
                .setParameter("famId", familyId)
                .setParameter("cutoffDate", thirtyDaysAgo)
                .getSingleResult();

            return count > 0;
        } finally {
            em.close();
        }
    }

    public List<Family> searchFamilies(String keyword) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Family> query = em.createQuery(
                "SELECT f FROM Family f WHERE LOWER(f.headName) LIKE LOWER(:kw) OR f.nationalId LIKE :kw", Family.class);
            query.setParameter("kw", "%" + keyword.trim() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean addFamily(Family family) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(family);
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

    public boolean updateFamily(Family family) {
        EntityManager em = getEntityManager();
        
        try {
        em.getTransaction().begin();
        em.merge(family);
        em.getTransaction().commit();
        return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFamily(int familyId) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Family family = em.find(Family.class, familyId);
            if (family != null) {
                em.remove(family);
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

    public List<Family> getAllFamilies() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT f FROM Family f", Family.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Family> getMostVulnerableFamilies() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT f FROM Family f ORDER BY CASE f.vulnerabilityLevel " +
                " WHEN 'HIGH' THEN 2 WHEN 'MEDIUM' THEN 3 WHEN 'LOW' THEN 4 ELSE 5 END, f.householdName ASC", 
                Family.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Family> getFamiliesNotYetServed() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT f FROM Family f WHERE f.familyId NOT IN (SELECT DISTINCT d.family.familyId FROM AidDistribution d)", 
                Family.class).getResultList();
        } finally {
            em.close();
        }
    }

    public long countTotalFamilies() {
        EntityManager em = getEntityManager();
        try { 
            return em.createQuery("SELECT COUNT(f) FROM Family f", Long.class).getSingleResult(); 
        } finally {
            em.close(); 
        }
    }

    public long countFamiliesServed() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(DISTINCT d.family) FROM AidDistribution d", Long.class).getSingleResult();
        } finally { 
            em.close();
        }
    }

    public long countFamiliesServedByOrg(int orgId) {
        EntityManager em = getEntityManager();
        try { 
            return em.createQuery("SELECT COUNT(DISTINCT d.family) FROM AidDistribution d WHERE d.organization.orgId = :orgId", Long.class)
                    .setParameter("orgId", orgId)
                    .getSingleResult(); 
        } finally { 
            em.close(); 
        }
    }
    
    
    public boolean isFamilyExists(String nationalId) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(f) FROM Family f WHERE f.nationalId = :nid", Long.class)
                           .setParameter("nid", nationalId)
                           .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}