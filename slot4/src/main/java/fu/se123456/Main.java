package fu.se123456;

import fu.se123456.dao.EmployeeDAO;
import fu.se123456.dao.ProjectDAO;
import fu.se123456.pojo.Employee;
import fu.se123456.pojo.Gender;
import fu.se123456.pojo.Project;
import fu.se123456.util.JPAUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println(" CHUONG TRINH DEMO: CHAPTER 1 - JPA MAPPING MANYTOMANY    ");
        System.out.println("            (HOAN THANH TU TODO 5.1 DEN 5.7)              ");
        System.out.println("==========================================================\n");

        EmployeeDAO employeeDAO = new EmployeeDAO();
        ProjectDAO projectDAO = new ProjectDAO();

        try {
            // ----------------------------------------------------------------------------------
            // 0. DỌN DẸP DỮ LIỆU CŨ TRƯỚC KHI CHẠY DEMO (TRÁNH LỖI TRÙNG UNIQUE KEY)
            // ----------------------------------------------------------------------------------
            jakarta.persistence.EntityManager emCleanup = JPAUtil.getEntityManager();
            jakarta.persistence.EntityTransaction txCleanup = emCleanup.getTransaction();
            try {
                txCleanup.begin();
                emCleanup.createNativeQuery("DELETE FROM employee_project").executeUpdate();
                emCleanup.createNativeQuery("DELETE FROM employees").executeUpdate();
                emCleanup.createNativeQuery("DELETE FROM projects").executeUpdate();
                txCleanup.commit();
            } catch (Exception e) {
                if (txCleanup.isActive()) txCleanup.rollback();
            } finally {
                emCleanup.close();
            }

            // ----------------------------------------------------------------------------------
            // KIỂM CHỨNG CHECKLIST: equals()/hashCode() DÙNG BUSINESS KEY (email)
            // ----------------------------------------------------------------------------------
            System.out.println(">>> [KIEM TRA EQUALS / HASHCODE DUNG BUSINESS KEY]");
            Set<Employee> testSet = new HashSet<>();
            Employee eDup1 = new Employee("Nguyen Van A", new BigDecimal("1500.00"), LocalDate.of(2023, 1, 15), "a.nguyen@company.com", Gender.MALE, true);
            Employee eDup2 = new Employee("Nguyen Van A Clone", new BigDecimal("1800.00"), LocalDate.of(2023, 2, 20), "a.nguyen@company.com", Gender.MALE, true);
            testSet.add(eDup1);
            testSet.add(eDup2); // Cùng email nhưng khác object reference
            System.out.println("Them 2 Employee co cung email vao Set. Kich thuoc Set = " + testSet.size() + " (Ky vong = 1)");
            if (testSet.size() == 1) {
                System.out.println("=> THANH CONG: equals()/hashCode() da loc trung dua tren business key email.\n");
            }

            // ----------------------------------------------------------------------------------
            // TODO 5.7: TẠO 3 EMPLOYEE, 2 PROJECT VÀ PHÂN CÔNG CHÉO
            // ----------------------------------------------------------------------------------
            System.out.println(">>> [TODO 5.7] KHOI TAO DU LIEU: 3 NHAN VIEN & 2 DU AN");
            Employee emp1 = new Employee("Nguyen Van A", new BigDecimal("1500.00"), LocalDate.of(2022, 3, 1), "emp1@example.com", Gender.MALE, true);
            Employee emp2 = new Employee("Tran Thi B", new BigDecimal("2000.00"), LocalDate.of(2021, 6, 15), "emp2@example.com", Gender.FEMALE, true);
            Employee emp3 = new Employee("Le Van C", new BigDecimal("1200.00"), LocalDate.of(2023, 1, 10), "emp3@example.com", Gender.OTHER, true);

            employeeDAO.save(emp1);
            employeeDAO.save(emp2);
            employeeDAO.save(emp3);
            System.out.println("Da luu 3 Employee vao Database: ID = " + emp1.getId() + ", " + emp2.getId() + ", " + emp3.getId());

            Project projA = new Project("PRJ-AI", "AI Smart System", new BigDecimal("50000.00"), LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
            Project projB = new Project("PRJ-CLOUD", "Cloud Migration", new BigDecimal("35000.00"), LocalDate.of(2024, 3, 1), null);

            projectDAO.save(projA);
            projectDAO.save(projB);
            System.out.println("Da luu 2 Project vao Database: ID = " + projA.getId() + ", " + projB.getId());

            // TODO 5.6 & 5.7: Phân công chéo bằng EmployeeDAO.assignEmployeeToProject
            System.out.println("\n>>> [TODO 5.6 & 5.7] THUC HIEN PHAN CONG CHEO:");
            System.out.println(" - NV1 (" + emp1.getFullName() + ") -> Project A + Project B");
            System.out.println(" - NV2 (" + emp2.getFullName() + ") -> Project B");
            System.out.println(" - NV3 (" + emp3.getFullName() + ") -> Project A");

            employeeDAO.assignEmployeeToProject(emp1.getId(), projA.getId());
            employeeDAO.assignEmployeeToProject(emp1.getId(), projB.getId());
            employeeDAO.assignEmployeeToProject(emp2.getId(), projB.getId());
            employeeDAO.assignEmployeeToProject(emp3.getId(), projA.getId());

            // In ra danh sách project của từng nhân viên
            System.out.println("\n--- DANH SACH DU AN CUA TUNG NHAN VIEN SAU KHI PHAN CONG ---");
            List<Employee> allEmployees = employeeDAO.findAll();
            for (Employee e : allEmployees) {
                Employee loaded = employeeDAO.findByIdWithProjects(e.getId());
                System.out.println("Nhan vien [" + loaded.getFullName() + " | Email: " + loaded.getEmail() + "] dang tham gia " + loaded.getProjects().size() + " du an:");
                for (Project p : loaded.getProjects()) {
                    System.out.println("   + [" + p.getProjectCode() + "] " + p.getProjectName() + " (Budget: " + p.getBudget() + ")");
                }
            }

            // ----------------------------------------------------------------------------------
            // TODO 5.8: DEMO JPQL ĐẾM SỐ NHÂN VIÊN ACTIVE VÀ TỔNG SALARY THEO TỪNG PROJECT
            // ----------------------------------------------------------------------------------
            System.out.println("\n>>> [TODO 5.8] DEMO JPQL: DEM SO NHAN VIEN ACTIVE VA TONG LUONG THEO PROJECT:");
            List<Object[]> projectStats = projectDAO.findActiveEmployeeStatsPerProject();
            for (Object[] row : projectStats) {
                String projectName = (String) row[0];
                Long activeCount = (Long) row[1];
                BigDecimal totalSalary = (BigDecimal) row[2];
                System.out.println(" - Du an [" + projectName + "]: So NV active = " + activeCount + ", Tong luong = " + totalSalary);
            }

            // ----------------------------------------------------------------------------------
            // TODO 5.9: DEMO UNASSIGN EMPLOYEE FROM PROJECT (GỠ KHỎI DỰ ÁN)
            // ----------------------------------------------------------------------------------
            System.out.println("\n>>> [TODO 5.9] DEMO GO 1 NHAN VIEN KHOI 1 PROJECT (unassignFromProject):");
            jakarta.persistence.EntityManager emCheck = JPAUtil.getEntityManager();
            long countJoinBefore = ((Number) emCheck.createNativeQuery("SELECT COUNT(*) FROM employee_project").getSingleResult()).longValue();
            long countEmpBefore = ((Number) emCheck.createNativeQuery("SELECT COUNT(*) FROM employees").getSingleResult()).longValue();
            long countProjBefore = ((Number) emCheck.createNativeQuery("SELECT COUNT(*) FROM projects").getSingleResult()).longValue();
            emCheck.close();

            System.out.println(" - Truoc khi go: Bang employee_project = " + countJoinBefore + " dong, Employees = " + countEmpBefore + ", Projects = " + countProjBefore);
            System.out.println(" - Tien hanh go NV1 (" + emp1.getFullName() + ") khoi Project B (" + projB.getProjectName() + ")...");

            employeeDAO.unassignEmployeeFromProject(emp1.getId(), projB.getId());

            jakarta.persistence.EntityManager emCheckAfter = JPAUtil.getEntityManager();
            long countJoinAfter = ((Number) emCheckAfter.createNativeQuery("SELECT COUNT(*) FROM employee_project").getSingleResult()).longValue();
            long countEmpAfter = ((Number) emCheckAfter.createNativeQuery("SELECT COUNT(*) FROM employees").getSingleResult()).longValue();
            long countProjAfter = ((Number) emCheckAfter.createNativeQuery("SELECT COUNT(*) FROM projects").getSingleResult()).longValue();
            emCheckAfter.close();

            System.out.println(" - Sau khi go: Bang employee_project = " + countJoinAfter + " dong, Employees = " + countEmpAfter + ", Projects = " + countProjAfter);
            if (countJoinBefore - countJoinAfter == 1 && countEmpBefore == countEmpAfter && countProjBefore == countProjAfter) {
                System.out.println("=> XAC NHAN: Bang employee_project mat dung 1 dong (" + countJoinBefore + " -> " + countJoinAfter + ").");
                System.out.println("=> XAC NHAN: Khong anh huong Employee hoac Project goc (van nguyen ven trong database).");
            }

            // ----------------------------------------------------------------------------------
            // TODO 5.10: DEMO JPQL TÌM NHÂN VIÊN ACTIVE THAM GIA NHIỀU HƠN 1 PROJECT CÙNG LÚC
            // ----------------------------------------------------------------------------------
            System.out.println("\n>>> [TODO 5.10] DEMO JPQL TIM NHAN VIEN (ACTIVE = TRUE) THAM GIA > 1 PROJECT:");
            System.out.println(" - Phan cong NV2 (" + emp2.getFullName() + ") tham gia them vao Project A (" + projA.getProjectName() + ")...");
            employeeDAO.assignEmployeeToProject(emp2.getId(), projA.getId());

            List<Employee> multiProjectEmps = employeeDAO.findActiveEmployeesWithMultipleProjects();
            System.out.println(" - Ket qua truy van (SELECT e FROM Employee e WHERE e.active = true AND SIZE(e.projects) > 1):");
            for (Employee e : multiProjectEmps) {
                Employee loaded = employeeDAO.findByIdWithProjects(e.getId());
                System.out.println("   + [" + loaded.getFullName() + " | Email: " + loaded.getEmail() + "] - Dang tham gia " + loaded.getProjects().size() + " du an (Active: " + loaded.isActive() + ")");
            }

            System.out.println("\n==========================================================");
            System.out.println(" HOAN THANH XONG TODO 5.1 DEN TODO 5.10!                  ");
            System.out.println("==========================================================");

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            JPAUtil.close();
        }
    }
}
