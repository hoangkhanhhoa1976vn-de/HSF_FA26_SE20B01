package fu.se123456.pojo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_code", unique = true, nullable = false, length = 100)
    private String projectCode;

    @Column(name = "project_name", nullable = false, length = 255)
    private String projectName;

    @Column(name = "budget", precision = 18, scale = 2)
    private BigDecimal budget;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    /*
     * TODO 5.3 — Trong Project (inverse side):
     * Quan hệ ManyToMany được ánh xạ ngược qua thuộc tính 'projects' ở phía Employee.
     *
     * LƯU Ý CHECKLIST:
     * KHÔNG dùng cascade = ALL hay cascade = REMOVE ở quan hệ N-N!
     * Lý do: N-N là quan hệ độc lập giữa 2 thực thể tồn tại song song.
     * Nếu để cascade = REMOVE, khi xóa 1 Project, Hibernate sẽ xóa luôn các Employee
     * đang tham gia dự án đó, điều này là sai nghiệp vụ nghiêm trọng.
     */
    @ManyToMany(mappedBy = "projects")
    private Set<Employee> employees = new HashSet<>();

    public Project() {
    }

    public Project(String projectCode, String projectName, BigDecimal budget, LocalDate startDate, LocalDate endDate) {
        this.projectCode = projectCode;
        this.projectName = projectName;
        this.budget = budget;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    /*
     * TODO 5.4 — Override equals()/hashCode() dựa trên projectCode — KHÔNG dùng id:
     *
     * GIẢI THÍCH (Mục 9.4 Chapter 1):
     * 1. Khi đối tượng Project mới tạo (transient), id = null. Nếu dùng id để băm thì
     *    hashCode dựa trên null. Khi entity được persist và sinh id từ DB (IDENTITY),
     *    hashCode sẽ thay đổi làm hỏng cấu trúc bucket của HashSet/HashMap, dẫn tới việc
     *    không thể tìm thấy hay xóa đối tượng khỏi Set.
     * 2. 'projectCode' là mã nghiệp vụ duy nhất (natural business key), bất biến và không null,
     *    đảm bảo hợp đồng của equals() và hashCode() luôn đúng trong mọi trạng thái của Entity.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return Objects.equals(projectCode, project.projectCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectCode);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", projectCode='" + projectCode + '\'' +
                ", projectName='" + projectName + '\'' +
                ", budget=" + budget +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
