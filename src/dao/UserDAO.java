package dao;

import javax.persistence.*;
import java.util.List;
import models.User;

public class UserDAO {
    private EntityManagerFactory emf;

    public UserDAO() {
        this.emf = Persistence.createEntityManagerFactory("GHADSPU");
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public User login(String username, String password, String role) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :user AND u.password = :pass AND u.role = :role", User.class);
            query.setParameter("user", username);
            query.setParameter("pass", password);
            query.setParameter("role", role);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public boolean isUsernameOrEmailExists(String username, String email) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.username = :user OR u.email = :email", Long.class)
                .setParameter("user", username)
                .setParameter("email", email)
                .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public boolean updatePassword(int userId, String currentPassword, String newPassword) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            User user = em.find(User.class, userId);
            if (user != null && user.getPassword().equals(currentPassword)) {
                user.setPassword(newPassword);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean addUser(User user) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(user);
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

    public boolean updateUser(User user) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean deleteUser(int userId) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            User user = em.find(User.class, userId);
            if (user != null) {
                em.remove(user);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public List<User> getAllCoordinators() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.role = 'COORDINATOR'", User.class).getResultList();
        } finally {
            em.close();
        }
    }

    public long countTotalOrganizations() {
        EntityManager em = getEntityManager();
        try { 
            return em.createQuery("SELECT COUNT(o) FROM Organization o", Long.class).getSingleResult(); 
        } finally { 
            em.close(); 
        }
    }

    public long countTotalCoordinators() {
        EntityManager em = getEntityManager();
        try { 
            return em.createQuery("SELECT COUNT(u) FROM User u WHERE u.role = 'COORDINATOR'", Long.class).getSingleResult(); 
        } finally { 
            em.close(); 
        }
    }
    
    
    public User findUserById(int userId) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, userId);
        } finally {
            em.close();
        }
    }
    
}