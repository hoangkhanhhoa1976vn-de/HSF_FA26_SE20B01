package fu.se123456;

import fu.se123456.dao.DepartmentDAO;
import fu.se123456.dao.EmployeeDAO;
import fu.se123456.pojo.Department;
import fu.se123456.pojo.Employee;
import fu.se123456.pojo.Gender;
import fu.se123456.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("         THUC HANH 2: CRUD JPA MAPPING (OneToMany / ManyToOne)   ");
        System.out.println("=================================================================\n");

        DepartmentDAO departmentDAO = new DepartmentDAO();
        EmployeeDAO employeeDAO = new EmployeeDAO();

        // ----------------------------------------------------------------------
        // KIEM CHUNG NHANH TODO 2.4: Helper method đồng bộ 2 chiều
        // ----------------------------------------------------------------------
        System.out.println(">>> KIEM CHUNG NHANH TODO 2.4 (Helper method dong bo 2 chieu):");
        Department deptTest = new Department("IT", "Ha Noi");
        Employee empTest = new Employee("test@company.com", "Test", Gender.OTHER, new BigDecimal("1000"), LocalDate.now());
        deptTest.addEmployee(empTest);
        System.out.println(deptTest.getEmployees().contains(empTest)); // phải true
        System.out.println(empTest.getDepartment() == deptTest);        // phải true
        System.out.println();

        // ----------------------------------------------------------------------
        // TODO 2.7: Demo trong Main
        // ----------------------------------------------------------------------
        System.out.println(">>> TODO 2.7: Demo Department cascade persist va JOIN FETCH");
        try {
            // 1) Tao Department + 3 Employee, add qua helper method (TODO 2.4)
            // Dung ten phong ban duy nhat theo thoi gian de tranh trung lap khi chay lai nhieu lan
            String deptName = "Marketing_" + System.currentTimeMillis();
            Department it = new Department(deptName, "Ha Noi");

            long ts = System.currentTimeMillis();
            Employee e1 = new Employee("aa." + ts + "@company.com", "Nguyen Van A", Gender.MALE,
                    new BigDecimal("15000000"), LocalDate.of(2022, 1, 10));
            Employee e2 = new Employee("bb." + ts + "@company.com", "Tran Thi B", Gender.FEMALE,
                    new BigDecimal("18000000"), LocalDate.of(2021, 6, 11));
            Employee e3 = new Employee("cc." + ts + "@company.com", "Le Van C", Gender.OTHER,
                    new BigDecimal("12000000"), LocalDate.of(2023, 3, 15));

            it.addEmployee(e1);
            it.addEmployee(e2);
            it.addEmployee(e3);

            // 2) Chi persist(department) - cascade = ALL tu lo phan Employee (TODO 2.7)
            departmentDAO.save(it);
            System.out.println("Da luu Department, id = " + it.getId());

            // 3) Tim lai kem employees bang JOIN FETCH (TODO 2.6) - khong bi
            // LazyInitializationException du EntityManager cua lan tim nay da dong,
            // vi employees da duoc load ngay trong cung 1 query.
            Department found = departmentDAO.findByIdWithEmployees(it.getId());
            if (found != null) {
                System.out.println("Phong ban: " + found.getName() + " (Location: " + found.getLocation() + ")");
                for (Employee e : found.getEmployees()) {
                    System.out.println(" - " + e);
                }
            }

            // 4) Thu save() them 1 Employee dung lai email da ton tai -> phai nem exception vi pham unique
            System.out.println("\n--- Kiem tra rang buoc Unique Email (Thu insert email trung) ---");
            try {
                Employee duplicateEmp = new Employee("aa." + ts + "@company.com", "Nguyen Van A Duplicate",
                        Gender.MALE, new BigDecimal("20000000"), LocalDate.now());
                duplicateEmp.setDepartment(it);
                employeeDAO.save(duplicateEmp);
                System.out.println("CANH BAO: Khong bat duoc exception duplicate email!");
            } catch (Exception ex) {
                System.out.println("Thanh cong: Da bat ngoai le khi vi pham rang buoc Unique Email:");
                System.out.println(" >> " + ex.getMessage());
            }

            // ----------------------------------------------------------------------
            // Tao them 1 Department de demo N+1 ro rang (it nhat 2 phong ban tro len)
            // ----------------------------------------------------------------------
            Department hr = new Department("HR_" + System.currentTimeMillis(), "Da Nang");
            Employee e4 = new Employee("dd." + ts + "@company.com", "Pham Thi D", Gender.FEMALE,
                    new BigDecimal("14000000"), LocalDate.of(2022, 5, 20));
            hr.addEmployee(e4);
            departmentDAO.save(hr);

            // ----------------------------------------------------------------------
            // TODO 2.8: Tai hien N+1 Query Problem
            // ----------------------------------------------------------------------
            System.out.println("\n=================================================================");
            System.out.println(">>> TODO 2.8: TAI HIEN N+1 QUERY PROBLEM");
            System.out.println("=================================================================");
            System.out.println("[Ghi chu ly thuyet]:");
            System.out.println(" - 1 cau SELECT de lay danh sach tat ca Department");
            System.out.println(" - N cau SELECT rieng biet de load Lazy danh sach Employees cho N phong ban");
            System.out.println(" - Tong cong: 1 + N cau SQL.");
            System.out.println("-----------------------------------------------------------------");

            EntityManager emNPlusOne = JPAUtil.getEMF().createEntityManager();
            try {
                // Cau 1: SELECT departments khong JOIN FETCH
                List<Department> deptList = emNPlusOne.createQuery("SELECT d FROM Department d", Department.class).getResultList();
                System.out.println("Da lay duoc " + deptList.size() + " phong ban tu DB.");

                // N cau tiep theo: Moi phong ban khi goi .getEmployees() se ban them 1 query SELECT rieng
                int deptCount = 0;
                for (Department d : deptList) {
                    deptCount++;
                    System.out.println("Phong ban #" + deptCount + ": " + d.getName() + " co " + d.getEmployees().size() + " nhan vien");
                }
            } finally {
                emNPlusOne.close();
            }

            // ----------------------------------------------------------------------
            // TODO 2.9: Fix N+1 bang JOIN FETCH
            // ----------------------------------------------------------------------
            System.out.println("\n=================================================================");
            System.out.println(">>> TODO 2.9: FIX N+1 BANG JOIN FETCH");
            System.out.println("=================================================================");
            System.out.println("[Ghi chu so sanh]:");
            System.out.println(" - Truoc fix: 1 + N cau SQL (1 SELECT departments + N SELECT employees)");
            System.out.println(" - Sau fix: Chi dung 1 cau SQL duy nhat co JOIN FETCH");
            System.out.println("-----------------------------------------------------------------");

            List<Department> fixedList = departmentDAO.findAllWithEmployees();
            int fixedCount = 0;
            for (Department d : fixedList) {
                fixedCount++;
                System.out.println("Phong ban #" + fixedCount + ": " + d.getName() + " co " + d.getEmployees().size() + " nhan vien");
            }
            System.out.println("=> Da load toan bo Department kem Employees chi bang 1 cau SQL JOIN FETCH!");

        } finally {
            JPAUtil.close();
            System.out.println("\n>>> Da dong EntityManagerFactory an toan.");
        }
    }
}
