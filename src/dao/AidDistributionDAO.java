package dao;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import models.AidDistribution;
import models.Family;

public class AidDistributionDAO {
    private EntityManagerFactory emf;

    public AidDistributionDAO() {
        this.emf = Persistence.createEntityManagerFactory("GHADSPU");
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public boolean recordAidDistribution(AidDistribution dist) throws DuplicateAidException {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            Family fam = em.find(Family.class, dist.getFamily().getFamilyId());
            String level = fam.getVulnerabilityLevel();

            if ("MEDIUM".equalsIgnoreCase(level) || "LOW".equalsIgnoreCase(level)) {
                
                TypedQuery<AidDistribution> query = em.createQuery(
                    "SELECT d FROM AidDistribution d WHERE d.family.familyId = :famId ORDER BY d.distributionDate DESC", AidDistribution.class)
                    .setParameter("famId", fam.getFamilyId())
                    .setMaxResults(1);
                
                List<AidDistribution> history = query.getResultList();
                
                if (!history.isEmpty()) {
                    AidDistribution lastDist = history.get(0);
                    
                    long daysBetween = ChronoUnit.DAYS.between(lastDist.getDistributionDate(), dist.getDistributionDate());
                    
                    if (daysBetween < 30) {
                        throw new DuplicateAidException(
                            fam.getHouseholdName(),
                            level,
                            lastDist.getOrganization().getName(),
                            lastDist.getDistributionDate()
                        );
                    }
                }
            }

            tx.begin();
            em.persist(dist);
            
            fam.setLastAidDate(dist.getDistributionDate());
            em.merge(fam);
            
            tx.commit();
            return true;
            
        } catch (DuplicateAidException e) {
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public List<AidDistribution> getAllDistributions() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT d FROM AidDistribution d", AidDistribution.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<AidDistribution> searchByOrganization(int orgId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT d FROM AidDistribution d WHERE d.organization.orgId = :orgId", AidDistribution.class)
                    .setParameter("orgId", orgId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    
    
    public long countTotalDistributions() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(d) FROM AidDistribution d", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
    
}