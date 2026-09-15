package fu.se123456.dao;

import fu.se123456.pojo.Department;
import fu.se123456.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;

public class DepartmentDAO {

    // TODO 2.5: save
    public void save(Department department) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(department);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // TODO 2.5: findById
    public Department findById(int id) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try {
            return em.find(Department.class, id);
        } finally {
            em.close();
        }
    }

    // TODO 2.5: findAll
    public List<Department> findAll() {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try {
            TypedQuery<Department> query = em.createQuery("SELECT d FROM Department d", Department.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // TODO 2.5: update (dùng em.merge() và gán lại kết quả)
    public Department update(Department department) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Department updated = em.merge(department);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // TODO 2.5: delete
    public void delete(int id) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Department department = em.find(Department.class, id);
            if (department != null) {
                em.remove(department);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // TODO 2.6: JPQL JOIN FETCH lấy 1 Department kèm danh sách Employee trong 1 lần
    public Department findByIdWithEmployees(int id) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try {
            TypedQuery<Department> query = em.createQuery(
                    "SELECT d FROM Department d JOIN FETCH d.employees WHERE d.id = :id",
                    Department.class
            );
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    // TODO 2.9: Fix N+1 bằng JOIN FETCH cho toàn bộ Departments
    public List<Department> findAllWithEmployees() {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try {
            TypedQuery<Department> query = em.createQuery(
                    "SELECT DISTINCT d FROM Department d JOIN FETCH d.employees",
                    Department.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
