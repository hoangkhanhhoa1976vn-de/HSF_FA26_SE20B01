package fu.se123456.dao;

import fu.se123456.pojo.Employee;
import fu.se123456.pojo.Project;
import fu.se123456.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class EmployeeDAO {

    public void save(Employee employee) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(employee);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Employee findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Employee.class, id);
        } finally {
            em.close();
        }
    }

    public List<Employee> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void update(Employee employee) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(employee);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee employee = em.find(Employee.class, id);
            if (employee != null) {
                em.remove(employee);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /*
     * TODO 5.6 — Viết EmployeeDAO với method assignEmployeeToProject(Long employeeId, Long projectId):
     * find cả 2 entity trong 1 transaction rồi gọi assignToProject().
     */
    public void assignEmployeeToProject(Long employeeId, Long projectId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee employee = em.find(Employee.class, employeeId);
            Project project = em.find(Project.class, projectId);

            if (employee != null && project != null) {
                // Gọi helper method để đồng bộ 2 chiều trong cùng transaction
                employee.assignToProject(project);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /*
     * TODO 5.9 — Viết method unassignEmployeeFromProject(Long employeeId, Long projectId):
     * find cả 2 entity trong 1 transaction rồi gọi unassignFromProject().
     */
    public void unassignEmployeeFromProject(Long employeeId, Long projectId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee employee = em.find(Employee.class, employeeId);
            Project project = em.find(Project.class, projectId);

            if (employee != null && project != null) {
                // Gọi helper method để gỡ đồng bộ 2 chiều trong cùng transaction
                employee.unassignFromProject(project);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Helper nạp Employee kèm Projects tránh LazyInitializationException ngoài session
    public Employee findByIdWithProjects(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Employee e LEFT JOIN FETCH e.projects WHERE e.id = :id";
            List<Employee> list = em.createQuery(jpql, Employee.class)
                    .setParameter("id", id)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }

    /*
     * TODO 5.10 — Viết JPQL tìm các Employee (chỉ lấy active = true) đang tham gia nhiều hơn 1 project cùng lúc:
     * SELECT e FROM Employee e WHERE e.active = true AND SIZE(e.projects) > 1
     */
    public List<Employee> findActiveEmployeesWithMultipleProjects() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Employee e WHERE e.active = true AND SIZE(e.projects) > 1";
            return em.createQuery(jpql, Employee.class).getResultList();
        } finally {
            em.close();
        }
    }
}
