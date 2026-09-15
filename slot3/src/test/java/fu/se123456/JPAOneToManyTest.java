package fu.se123456;

import fu.se123456.dao.DepartmentDAO;
import fu.se123456.dao.EmployeeDAO;
import fu.se123456.pojo.Department;
import fu.se123456.pojo.Employee;
import fu.se123456.pojo.Gender;
import fu.se123456.util.JPAUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JPAOneToManyTest {

    private static DepartmentDAO departmentDAO;
    private static EmployeeDAO employeeDAO;

    @BeforeAll
    public static void setUp() {
        departmentDAO = new DepartmentDAO();
        employeeDAO = new EmployeeDAO();
    }

    @AfterAll
    public static void tearDown() {
        JPAUtil.close();
    }

    @Test
    public void testTODO2_4_BidirectionalHelperMethods() {
        Department dept = new Department("R&D", "Da Nang");
        Employee emp = new Employee("emp.rd@company.com", "R&D Engineer", Gender.MALE,
                new BigDecimal("25000000"), LocalDate.now());

        // Test addEmployee
        dept.addEmployee(emp);
        assertTrue(dept.getEmployees().contains(emp), "Department's employees list must contain the added employee");
        assertEquals(dept, emp.getDepartment(), "Employee's department must be synchronized to this department");

        // Test removeEmployee
        dept.removeEmployee(emp);
        assertFalse(dept.getEmployees().contains(emp), "Department's employees list must not contain removed employee");
        assertNull(emp.getDepartment(), "Employee's department must be null after removal");
    }

    @Test
    public void testTODO2_7_CascadePersistAndJoinFetch() {
        long ts = System.currentTimeMillis();
        Department dept = new Department("Dev_" + ts, "HCM");

        Employee e1 = new Employee("dev1_" + ts + "@company.com", "Dev One", Gender.FEMALE,
                new BigDecimal("16000000"), LocalDate.of(2023, 1, 15));
        Employee e2 = new Employee("dev2_" + ts + "@company.com", "Dev Two", Gender.MALE,
                new BigDecimal("19000000"), LocalDate.of(2023, 2, 20));

        dept.addEmployee(e1);
        dept.addEmployee(e2);

        // Persist department - cascade ALL automatically persists e1 and e2
        departmentDAO.save(dept);
        assertTrue(dept.getId() > 0, "Department ID should be generated");
        assertTrue(e1.getId() > 0, "Employee e1 ID should be generated via cascade");
        assertTrue(e2.getId() > 0, "Employee e2 ID should be generated via cascade");

        // Test TODO 2.6: findByIdWithEmployees using JPQL JOIN FETCH
        Department fetched = departmentDAO.findByIdWithEmployees(dept.getId());
        assertNotNull(fetched, "Fetched department must not be null");
        assertEquals(2, fetched.getEmployees().size(), "Should have 2 employees eagerly fetched");
    }

    @Test
    public void testCascadeDeleteAndOrphanRemoval() {
        long ts = System.currentTimeMillis();
        Department dept = new Department("TempDept_" + ts, "Can Tho");
        Employee e = new Employee("temp_" + ts + "@company.com", "Temp Emp", Gender.OTHER,
                new BigDecimal("10000000"), LocalDate.now());
        dept.addEmployee(e);
        departmentDAO.save(dept);

        int deptId = dept.getId();
        int empId = e.getId();

        // Delete department -> cascade ALL should delete employee e
        departmentDAO.delete(deptId);

        assertNull(departmentDAO.findById(deptId), "Department must be deleted");
        assertNull(employeeDAO.findById(empId), "Employee must be deleted via cascade");
    }

    @Test
    public void testUniqueEmailConstraint() {
        long ts = System.currentTimeMillis();
        Department dept = new Department("Finance_" + ts, "Ha Noi");
        departmentDAO.save(dept);

        Employee e1 = new Employee("finance_" + ts + "@company.com", "Accountant 1", Gender.FEMALE,
                new BigDecimal("13000000"), LocalDate.now());
        e1.setDepartment(dept);
        employeeDAO.save(e1);

        // Attempting to save another employee with same email must throw Exception
        Employee e2 = new Employee("finance_" + ts + "@company.com", "Accountant 2", Gender.MALE,
                new BigDecimal("13000000"), LocalDate.now());
        e2.setDepartment(dept);

        assertThrows(Exception.class, () -> employeeDAO.save(e2),
                "Saving duplicate email must violate unique constraint and throw an exception");
    }
}
