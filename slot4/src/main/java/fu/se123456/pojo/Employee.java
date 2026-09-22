package fu.se123456.pojo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "salary", precision = 18, scale = 2)
    private BigDecimal salary;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    /*
     * TODO 5.2 — Trong Employee (owning side):
     * Cấu hình @ManyToMany với bảng trung gian 'employee_project',
     * joinColumns trỏ tới khóa ngoại employee_id, inverseJoinColumns trỏ tới project_id.
     * Dùng Set<> để ngăn ngừa trùng lặp phần tử.
     *
     * LƯU Ý CHECKLIST:
     * KHÔNG dùng cascade = ALL / REMOVE ở quan hệ N-N!
     * Vì nếu xóa một Employee, ta KHÔNG được phép xóa Project chung
     * (dự án đó vẫn đang do các nhân viên khác thực hiện).
     */
    @ManyToMany
    @JoinTable(
        name = "employee_project",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private Set<Project> projects = new HashSet<>();

    public Employee() {
    }

    public Employee(String fullName, BigDecimal salary, LocalDate hireDate, String email, Gender gender, boolean active) {
        this.fullName = fullName;
        this.salary = salary;
        this.hireDate = hireDate;
        this.email = email;
        this.gender = gender;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    /*
     * TODO 5.5 — Viết helper method assignToProject(Project p) trong Employee
     * để add vào cả 2 phía: đồng bộ cả employee.getProjects() và p.getEmployees().
     */
    public void assignToProject(Project p) {
        if (p != null) {
            this.projects.add(p);
            p.getEmployees().add(this);
        }
    }

    /*
     * TODO 5.9 — Viết helper method unassignFromProject(Project p) (gỡ khỏi dự án):
     * Đồng bộ cả 2 phía: xóa Project khỏi employee.getProjects() và xóa Employee khỏi p.getEmployees().
     */
    public void unassignFromProject(Project p) {
        if (p != null) {
            this.projects.remove(p);
            p.getEmployees().remove(this);
        }
    }


    /*
     * TODO 5.4 — Override equals()/hashCode() dựa trên email — KHÔNG dùng id:
     *
     * GIẢI THÍCH (Mục 9.4 Chapter 1):
     * 1. Khi một Employee vừa được tạo mới (transient state), id = null. Nếu dùng id để tính hashCode,
     *    đối tượng được đưa vào một bucket dựa trên null. Sau khi persist xuống DB, database sinh id mới
     *    (IDENTITY), làm thay đổi hashCode của đối tượng. Khi đó, nếu tìm kiếm trong Set/Map, ta sẽ
     *    không thể tìm thấy đối tượng nữa (vi phạm tính bất biến của Hash Table).
     * 2. 'email' là thuộc tính tự nhiên duy nhất (unique business key), không thay đổi trong suốt vòng đời
     *    của đối tượng nhân viên, đảm bảo rằng 2 instance đại diện cho cùng 1 nhân viên trong đời thực
     *    sẽ có cùng mã hash và được Set nhận diện chính xác, tránh duplicate.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", salary=" + salary +
                ", hireDate=" + hireDate +
                ", email='" + email + '\'' +
                ", gender=" + gender +
                ", active=" + active +
                '}';
    }
}
