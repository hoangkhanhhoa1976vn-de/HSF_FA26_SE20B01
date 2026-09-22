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

    /*
     * TODO 5.11 — Viết method deactivateEmployee(Long employeeId) (set active = false)
     *
     * GIẢI THÍCH TRONG COMMENT (Theo yêu cầu Checklist):
     *
     * 1. Câu hỏi: Nhân viên nghỉ việc có nên tự động bị gỡ khỏi tất cả project hay không?
     *    -> TRẢ LỜI: KHÔNG NÊN tự động gỡ nhân viên khỏi các project (không xóa dữ liệu trong bảng trung gian employee_project).
     *
     * 2. Lý do cần bảo toàn quan hệ trong employee_project:
     *    - Tính toàn vẹn lịch sử (Audit Trail & Historical Integrity): Dữ liệu nhân viên đã từng tham gia
     *      dự án nào, thực hiện công việc gì là tài sản thông tin lịch sử của công ty (phục vụ nghiệm thu,
     *      báo cáo tài chính, kiểm toán, đánh giá đóng góp nhân sự). Nếu gỡ nhân viên ra khỏi dự án khi họ nghỉ việc,
     *      toàn bộ dấu vết lịch sử tham gia dự án sẽ biến mất hoàn toàn, gây sai lệch báo cáo quá khứ.
     *
     * 3. Vì sao quan hệ N-N thường KHÔNG dùng cascade = REMOVE / cascade = ALL:
     *    - Employee và Project là 2 thực thể độc lập có vòng đời riêng biệt.
     *    - Nếu cấu hình cascade REMOVE trên quan hệ N-N, khi xóa một Employee, Hibernate sẽ xóa luôn các
     *      Project mà nhân viên đó tham gia (dù dự án vẫn đang thực hiện bởi các nhân viên khác). Tương tự,
     *      khi xóa 1 Project, hệ thống tuyệt đối không được phép xóa các Employee của doanh nghiệp.
     *    - Do đó, quan hệ N-N luôn tránh cascade REMOVE để không xóa nhầm thực thể phía bên kia.
     *
     * 4. Cách xử lý phù hợp (Soft Delete):
     *    - Chỉ cập nhật cờ trạng thái 'active = false' của Employee trong bảng employees.
     *    - Giữ nguyên toàn bộ các dòng liên kết trong bảng trung gian 'employee_project' để tra cứu lịch sử.
     *    - Trong các câu truy vấn thống kê nghiệp vụ hiện tại (như TODO 5.8 và TODO 5.10), ta luôn sử dụng
     *      điều kiện 'WHERE e.active = true' để lọc ra những nhân viên đang thực tế hoạt động.
     */
    public void deactivateEmployee(Long employeeId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee employee = em.find(Employee.class, employeeId);
            if (employee != null) {
                employee.setActive(false);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
