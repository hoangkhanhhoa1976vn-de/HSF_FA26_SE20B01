package fu.se123456;

import fu.se123456.dao.EmployeeDAO;
import fu.se123456.dao.ProjectDAO;
import fu.se123456.pojo.Employee;
import fu.se123456.pojo.Gender;
import fu.se123456.pojo.Project;
import fu.se123456.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class JPAManyToManyTest {

    private static EmployeeDAO employeeDAO;
    private static ProjectDAO projectDAO;

    @BeforeAll
    public static void setUp() {
        employeeDAO = new EmployeeDAO();
        projectDAO = new ProjectDAO();
    }

    @AfterAll
    public static void tearDown() {
        JPAUtil.close();
    }

    @Test
    public void testTODO5_4_BusinessKeyEqualsAndHashCode() {
        Set<Employee> set = new HashSet<>();
        Employee e1 = new Employee("Test User", new BigDecimal("1000.00"), LocalDate.now(), "unique_test@company.com", Gender.MALE, true);
        Employee e2 = new Employee("Test User Different Name", new BigDecimal("2000.00"), LocalDate.now(), "unique_test@company.com", Gender.FEMALE, true);

        set.add(e1);
        set.add(e2);

        assertEquals(1, set.size(), "Set must only contain 1 element because both employees have the same business key (email)");
    }

    @Test
    public void testTODO5_5_and_5_9_BidirectionalHelpers() {
        Employee emp = new Employee("Helper Emp", new BigDecimal("1500.00"), LocalDate.now(), "helper_emp@test.com", Gender.MALE, true);
        Project proj = new Project("PRJ-HELP", "Helper Project", new BigDecimal("10000.00"), LocalDate.now(), null);

        // Test assignToProject
        emp.assignToProject(proj);
        assertTrue(emp.getProjects().contains(proj), "Employee projects must contain project");
        assertTrue(proj.getEmployees().contains(emp), "Project employees must contain employee");

        // Test unassignFromProject
        emp.unassignFromProject(proj);
        assertFalse(emp.getProjects().contains(proj), "Employee projects must not contain project after unassign");
        assertFalse(proj.getEmployees().contains(emp), "Project employees must not contain employee after unassign");
    }

    @Test
    public void testTODO5_9_UnassignRemovesOnlyJoinRow() {
        long ts = System.currentTimeMillis();
        Employee emp = new Employee("Unassign Emp " + ts, new BigDecimal("1200.00"), LocalDate.now(), "unassign_" + ts + "@test.com", Gender.MALE, true);
        Project proj = new Project("PRJ-UNASSIGN-" + ts, "Unassign Project", new BigDecimal("20000.00"), LocalDate.now(), null);

        employeeDAO.save(emp);
        projectDAO.save(proj);

        employeeDAO.assignEmployeeToProject(emp.getId(), proj.getId());

        EntityManager em = JPAUtil.getEntityManager();
        long joinCountBefore = ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM employee_project WHERE employee_id = ?1 AND project_id = ?2")
                .setParameter(1, emp.getId())
                .setParameter(2, proj.getId())
                .getSingleResult()).longValue();
        em.close();
        assertEquals(1, joinCountBefore, "Join table must have 1 row for this assignment");

        // Unassign employee from project
        employeeDAO.unassignEmployeeFromProject(emp.getId(), proj.getId());

        em = JPAUtil.getEntityManager();
        long joinCountAfter = ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM employee_project WHERE employee_id = ?1 AND project_id = ?2")
                .setParameter(1, emp.getId())
                .setParameter(2, proj.getId())
                .getSingleResult()).longValue();
        em.close();

        assertEquals(0, joinCountAfter, "Join row must be deleted");
        assertNotNull(employeeDAO.findById(emp.getId()), "Employee must still exist in DB");
        assertNotNull(projectDAO.findById(proj.getId()), "Project must still exist in DB");
    }

    @Test
    public void testTODO5_10_FindActiveEmployeesWithMultipleProjects() {
        long ts = System.currentTimeMillis();
        Employee emp = new Employee("Multi Project Emp " + ts, new BigDecimal("3000.00"), LocalDate.now(), "multi_" + ts + "@test.com", Gender.FEMALE, true);
        Project p1 = new Project("PRJ-M1-" + ts, "P1", new BigDecimal("15000.00"), LocalDate.now(), null);
        Project p2 = new Project("PRJ-M2-" + ts, "P2", new BigDecimal("25000.00"), LocalDate.now(), null);

        employeeDAO.save(emp);
        projectDAO.save(p1);
        projectDAO.save(p2);

        employeeDAO.assignEmployeeToProject(emp.getId(), p1.getId());
        employeeDAO.assignEmployeeToProject(emp.getId(), p2.getId());

        List<Employee> list = employeeDAO.findActiveEmployeesWithMultipleProjects();
        boolean found = list.stream().anyMatch(e -> e.getId().equals(emp.getId()));
        assertTrue(found, "Active employee in 2 projects must be returned by JPQL query");
    }

    @Test
    public void testTODO5_11_DeactivateEmployeeKeepsJoinHistory() {
        long ts = System.currentTimeMillis();
        Employee emp = new Employee("Deact Emp " + ts, new BigDecimal("1800.00"), LocalDate.now(), "deact_" + ts + "@test.com", Gender.OTHER, true);
        Project p = new Project("PRJ-DEACT-" + ts, "Project Deact", new BigDecimal("18000.00"), LocalDate.now(), null);

        employeeDAO.save(emp);
        projectDAO.save(p);
        employeeDAO.assignEmployeeToProject(emp.getId(), p.getId());

        // Deactivate employee
        employeeDAO.deactivateEmployee(emp.getId());

        Employee foundEmp = employeeDAO.findById(emp.getId());
        assertNotNull(foundEmp);
        assertFalse(foundEmp.isActive(), "Employee active status must be false");

        // Verify join record is still intact for history/audit trail
        EntityManager em = JPAUtil.getEntityManager();
        long joinCount = ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM employee_project WHERE employee_id = ?1 AND project_id = ?2")
                .setParameter(1, emp.getId())
                .setParameter(2, p.getId())
                .getSingleResult()).longValue();
        em.close();

        assertEquals(1, joinCount, "Join table relation must be preserved for history");
    }
}
