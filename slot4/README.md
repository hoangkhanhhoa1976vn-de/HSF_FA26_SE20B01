# BÀI TẬP CHAPTER 1: JPA MAPPING MANYTOMANY (SLOT 4)
### PHẦN BÀI LÀM: TỪ TODO 5.1 ĐẾN TODO 5.11 (HOÀN THÀNH TOÀN BỘ)

---

## 1. Giới thiệu bài toán
Hệ thống quản lý phân công dự án:
- Mỗi **Employee** (Nhân viên) có thể tham gia nhiều **Project** (Dự án).
- Mỗi **Project** có thể có nhiều **Employee** tham gia.
- Quan hệ nhiều - nhiều ($N-N$) được ánh xạ thông qua bảng trung gian `employee_project`.
- **Owning side**: `Employee` (chứa `@JoinTable`).
- **Inverse side**: `Project` (chứa `mappedBy = "projects"`).

---

## 2. Bảng tổng hợp các TODO (5.1 đến 5.11)

| TODO | Mô tả yêu cầu | File triển khai |
| :--- | :--- | :--- |
| **TODO 5.1** | Tạo entity `Employee` và `Project`, dùng `Set<>` (không dùng `List<>`) để chống trùng lặp. | [Employee.java](file:///slot4/src/main/java/fu/se123456/pojo/Employee.java), [Project.java](file:///slot4/src/main/java/fu/se123456/pojo/Project.java) |
| **TODO 5.2** | Cấu hình `@ManyToMany` và `@JoinTable(name = "employee_project")` trong `Employee` (owning side). | [Employee.java](file:///slot4/src/main/java/fu/se123456/pojo/Employee.java) |
| **TODO 5.3** | Cấu hình `@ManyToMany(mappedBy = "projects")` trong `Project` (inverse side). | [Project.java](file:///slot4/src/main/java/fu/se123456/pojo/Project.java) |
| **TODO 5.4** | Override `equals()/hashCode()`: `Employee` theo `email`, `Project` theo `projectCode` (không dùng `id`, giải thích theo mục 9.4). | [Employee.java](file:///slot4/src/main/java/fu/se123456/pojo/Employee.java), [Project.java](file:///slot4/src/main/java/fu/se123456/pojo/Project.java) |
| **TODO 5.5** | Viết helper method `assignToProject(Project p)` đồng bộ cả 2 phía trong `Employee`. | [Employee.java](file:///slot4/src/main/java/fu/se123456/pojo/Employee.java) |
| **TODO 5.6** | Viết `EmployeeDAO.assignEmployeeToProject(Long employeeId, Long projectId)` trong 1 transaction. | [EmployeeDAO.java](file:///slot4/src/main/java/fu/se123456/dao/EmployeeDAO.java) |
| **TODO 5.7** | Viết `Main` demo: tạo 3 Employee (đủ lương, ngày vào làm, giới tính, active), 2 Project, phân công chéo và in danh sách dự án. | [Main.java](file:///slot4/src/main/java/fu/se123456/Main.java) |
| **TODO 5.8** | Viết JPQL đếm số nhân viên active tham gia mỗi project và tính tổng salary của các nhân viên đó. | [ProjectDAO.java](file:///slot4/src/main/java/fu/se123456/dao/ProjectDAO.java), [Main.java](file:///slot4/src/main/java/fu/se123456/Main.java) |
| **TODO 5.9** | Viết method `unassignFromProject(Project p)` và demo gỡ 1 nhân viên khỏi 1 project — xác nhận bảng `employee_project` mất đúng 1 dòng, không ảnh hưởng Employee/Project gốc. | [Employee.java](file:///slot4/src/main/java/fu/se123456/pojo/Employee.java), [EmployeeDAO.java](file:///slot4/src/main/java/fu/se123456/dao/EmployeeDAO.java), [Main.java](file:///slot4/src/main/java/fu/se123456/Main.java) |
| **TODO 5.10** | Viết JPQL tìm các Employee (chỉ lấy `active = true`) đang tham gia nhiều hơn 1 project cùng lúc (`SIZE(e.projects) > 1`). | [EmployeeDAO.java](file:///slot4/src/main/java/fu/se123456/dao/EmployeeDAO.java), [Main.java](file:///slot4/src/main/java/fu/se123456/Main.java) |
| **TODO 5.11** | Viết method `deactivateEmployee(Long employeeId)` (`active = false`) và giải thích chi tiết trong comment nghiệp vụ khi nhân viên nghỉ việc. | [EmployeeDAO.java](file:///slot4/src/main/java/fu/se123456/dao/EmployeeDAO.java), [Main.java](file:///slot4/src/main/java/fu/se123456/Main.java) |

---

## 3. Lý thuyết & Câu hỏi trọng tâm

### 3.1. Vì sao KHÔNG dùng `id` trong `equals()` và `hashCode()` mà dùng Business Key? (Mục 9.4 Chapter 1)
- **Vấn đề của ID**: Khi đối tượng vừa được khởi tạo bằng `new` (transient state), trường `id` mang giá trị `null`. Nếu đối tượng này được thêm vào `HashSet` hoặc `HashMap`, hàm băm sẽ tính toán dựa trên `null`. Sau khi `persist` xuống database, database sinh ID mới (IDENTITY), dẫn tới giá trị băm bị thay đổi. Khi đó, cấu trúc bucket trong Set bị phá vỡ, không thể tìm thấy hay xóa đối tượng khỏi Set.
- **Giải pháp Business Key**: Sử dụng trường định danh tự nhiên, duy nhất và bất biến trong suốt vòng đời của đối tượng (`email` của Employee, `projectCode` của Project). Khi hai đối tượng có cùng business key, chúng được coi là một thực thể ngay cả trước khi lưu vào DB.

### 3.2. Vì sao quan hệ N-N thường KHÔNG dùng `cascade = REMOVE` / `cascade = ALL`?
- **Tính độc lập của thực thể**: Trong quan hệ nhiều - nhiều, `Employee` và `Project` là 2 thực thể tồn tại song song, bình đẳng và độc lập.
- **Tránh xóa nhầm dữ liệu**: Nếu thiết lập `cascade = REMOVE`, khi xóa một dự án (`Project`), Hibernate có thể xóa nhầm luôn tất cả các nhân viên (`Employee`) tham gia dự án đó (trong khi họ vẫn đang làm việc tại công ty và tham gia các dự án khác). Ngược lại, xóa một nhân viên không được phép xóa mất dự án chung.

### 3.3. Nhân viên nghỉ việc có nên tự động bị gỡ khỏi tất cả project hay không?
- **KHÔNG NÊN gỡ tự động**: Toàn bộ lịch sử tham gia dự án trong bảng `employee_project` cần được giữ nguyên vẹn để phục vụ **Audit Trail** (kiểm toán), đánh giá năng suất, nghiệm thu và thống kê tài chính lịch sử.
- **Giải pháp chuẩn (Soft Delete)**: Gọi method `deactivateEmployee(Long id)` để chuyển trạng thái sang `active = false`. Khi thống kê các nhân viên đang hoạt động trong dự án, ta chỉ cần thêm điều kiện `WHERE e.active = true`.

---

## 4. Checklist hoàn thành 100%

- [x] Bảng trung gian `employee_project` được Hibernate tự tạo với 2 cột FK (`employee_id`, `project_id`) là composite key.
- [x] Phân công chéo nhiều nhân viên/project thành công, dữ liệu đúng ở cả 2 chiều (`employee.getProjects()` và `project.getEmployees()` khớp nhau).
- [x] `equals()/hashCode()` dùng business key — thêm 1 Employee vào `Set<Employee>` 2 lần (cùng email, khác object reference) $\rightarrow$ Set chỉ giữ 1 phần tử.
- [x] `unassignFromProject()` chỉ xóa dòng trong bảng trung gian, không xóa Employee hay Project gốc.
- [x] Query đếm số nhân viên active/tổng lương theo từng project (TODO 5.8) chạy đúng, kết quả khớp dữ liệu đã insert.
- [x] Query tìm nhân viên tham gia nhiều hơn 1 project (TODO 5.10) trả về đúng danh sách.
- [x] Không dùng `cascade = ALL` ở quan hệ N-N (đã giải thích trong comment và tài liệu).
- [x] `deactivateEmployee()` chỉ update cột `active = false`, không xóa quan hệ trong `employee_project`, dữ liệu tham gia project vẫn còn nguyên để tra cứu lịch sử.

---

## 5. Hướng dẫn chạy và kiểm thử

### Chạy Demo:
Mở IntelliJ IDEA hoặc chạy lệnh:
```powershell
mvn compile exec:java -Dexec.mainClass="fu.se123456.Main"
```

### Chạy Automated Tests:
Chạy lệnh kiểm thử Maven:
```powershell
mvn test
```
Tất cả các test cases kiểm tra business key, tính toàn vẹn 2 chiều của quan hệ ManyToMany, các helper methods, JPQL queries và logic soft delete đều đạt kết quả **100% SUCCESS**.
