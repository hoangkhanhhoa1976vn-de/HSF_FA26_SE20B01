# BÀI TẬP CHAPTER 1: JPA MAPPING MANYTOMANY (SLOT 4)
### PHẦN BÀI LÀM: TỪ TODO 5.1 ĐẾN TODO 5.7

---

## 1. Giới thiệu bài toán
Hệ thống quản lý phân công dự án:
- Mỗi **Employee** (Nhân viên) có thể tham gia nhiều **Project** (Dự án).
- Mỗi **Project** có thể có nhiều **Employee** tham gia.
- Quan hệ nhiều - nhiều ($N-N$) được ánh xạ thông qua bảng trung gian `employee_project`.
- **Owning side**: `Employee` (chứa `@JoinTable`).
- **Inverse side**: `Project` (chứa `mappedBy = "projects"`).

---

## 2. Bảng tổng hợp các TODO (5.1 đến 5.7)

| TODO | Mô tả yêu cầu | File triển khai |
| :--- | :--- | :--- |
| **TODO 5.1** | Tạo entity `Employee` và `Project`, dùng `Set<>` (không dùng `List<>`) để chống trùng lặp. | [Employee.java](file:///D:/Codenop/slot4/src/main/java/fu/se123456/pojo/Employee.java), [Project.java](file:///D:/Codenop/slot4/src/main/java/fu/se123456/pojo/Project.java) |
| **TODO 5.2** | Cấu hình `@ManyToMany` và `@JoinTable(name = "employee_project")` trong `Employee` (owning side). | [Employee.java](file:///D:/Codenop/slot4/src/main/java/fu/se123456/pojo/Employee.java) |
| **TODO 5.3** | Cấu hình `@ManyToMany(mappedBy = "projects")` trong `Project` (inverse side). | [Project.java](file:///D:/Codenop/slot4/src/main/java/fu/se123456/pojo/Project.java) |
| **TODO 5.4** | Override `equals()/hashCode()`: `Employee` theo `email`, `Project` theo `projectCode` (không dùng `id`, giải thích theo mục 9.4). | [Employee.java](file:///D:/Codenop/slot4/src/main/java/fu/se123456/pojo/Employee.java), [Project.java](file:///D:/Codenop/slot4/src/main/java/fu/se123456/pojo/Project.java) |
| **TODO 5.5** | Viết helper method `assignToProject(Project p)` đồng bộ cả 2 phía trong `Employee`. | [Employee.java](file:///D:/Codenop/slot4/src/main/java/fu/se123456/pojo/Employee.java) |
| **TODO 5.6** | Viết `EmployeeDAO.assignEmployeeToProject(Long employeeId, Long projectId)` trong 1 transaction. | [EmployeeDAO.java](file:///D:/Codenop/slot4/src/main/java/fu/se123456/dao/EmployeeDAO.java) |
| **TODO 5.7** | Viết `Main` demo: tạo 3 Employee (đủ lương, ngày vào làm, giới tính, active), 2 Project, phân công chéo và in danh sách dự án. | [Main.java](file:///D:/Codenop/slot4/src/main/java/fu/se123456/Main.java) |

---

## 3. Câu hỏi lý thuyết: Vì sao KHÔNG dùng `id` trong `equals()` và `hashCode()` mà dùng Business Key? (Mục 9.4 Chapter 1)
- **Vấn đề của ID**: Khi đối tượng vừa được khởi tạo bằng `new` (transient state), trường `id` mang giá trị `null`. Nếu đối tượng này được thêm vào `HashSet` hoặc `HashMap`, hàm băm sẽ tính toán dựa trên `null`. Sau khi `persist` xuống database, database sinh ID mới (IDENTITY), dẫn tới giá trị băm bị thay đổi. Khi đó, cấu trúc bucket trong Set bị phá vỡ, không thể tìm thấy hay xóa đối tượng khỏi Set.
- **Giải pháp Business Key**: Sử dụng trường định danh tự nhiên, duy nhất và bất biến trong suốt vòng đời của đối tượng (`email` của Employee, `projectCode` của Project). Khi hai đối tượng có cùng business key, chúng được coi là một thực thể ngay cả trước khi lưu vào DB.

---

## 4. Hướng dẫn chạy và kiểm thử

### Chạy Demo:
Mở IntelliJ IDEA và chạy file `Main.java` (`src/main/java/fu/se123456/Main.java`).

### Chạy Automated Tests:
Chạy lệnh kiểm thử Maven:
```powershell
mvn test
```
Tất cả các test cases kiểm tra business key và tính toàn vẹn 2 chiều của quan hệ ManyToMany đều đạt kết quả thành công 100%.
