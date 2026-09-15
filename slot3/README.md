# THỰC HÀNH 2: CRUD JPA MAPPING (OneToMany / ManyToOne)

Dự án Java JPA / Hibernate thực thi quan hệ 1-N giữa phòng ban (`Department`) và nhân viên (`Employee`), thực hiện các thao tác CRUD qua DAO, tối ưu truy vấn bằng JPQL `JOIN FETCH` và giải quyết bài toán N+1 Query Problem.

---

## 1. Cấu hình Cơ sở dữ liệu (SQL Server)

- **Database name**: `HSF302_Chapter1`
- **Port**: `1433`
- **Username**: `sa`
- **Password**: `yourPassword`
- **Cơ chế sinh bảng**: Tự động qua `hibernate.hbm2ddl.auto = update` trong `persistence.xml`.

### Script tạo Database & Bảng (nếu cần tạo thủ công):
```sql
-- 1. Tạo database
CREATE DATABASE HSF302_Chapter1;
GO

USE HSF302_Chapter1;
GO

-- 2. Tạo bảng departments
CREATE TABLE departments (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL UNIQUE,
    location NVARCHAR(255)
);
GO

-- 3. Tạo bảng employees
CREATE TABLE employees (
    id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(255),
    salary NUMERIC(18,2),
    hire_date DATE,
    email NVARCHAR(255) NOT NULL UNIQUE,
    gender NVARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    active BIT NOT NULL DEFAULT 1,
    department_id INT NOT NULL,
    CONSTRAINT FK_employees_departments FOREIGN KEY (department_id) 
        REFERENCES departments(id) ON DELETE CASCADE
);
GO
```

---

## 2. Cấu trúc thư mục & Package

```
src
├── main
│   ├── java
│   │   └── fu
│   │       └── se123456
│   │           ├── Main.java                   # Hàm main chạy demo toàn bộ TODO
│   │           ├── dao
│   │           │   ├── DepartmentDAO.java      # CRUD Department, JOIN FETCH, findAllWithEmployees
│   │           │   └── EmployeeDAO.java        # CRUD Employee
│   │           ├── pojo
│   │           │   ├── Department.java         # Entity Department (Inverse side, OneToMany)
│   │           │   ├── Employee.java           # Entity Employee (Owning side, ManyToOne)
│   │           │   └── Gender.java             # Enum Gender (MALE, FEMALE, OTHER)
│   │           └── util
│   │               └── JPAUtil.java            # Singleton EntityManagerFactory (hsf302PU)
│   └── resources
│       └── META-INF
│           └── persistence.xml                 # Cấu hình JPA & Hibernate SQL Server
└── test
    └── java
        └── fu
            └── se123456
                └── JPAOneToManyTest.java       # Bộ kiểm thử JUnit 5 tự động
```

---

## 3. Chi tiết các TODO trong bài

- **TODO 1: Cấu hình dự án**
  - Khai báo dependency Hibernate Core 6.5.2, MSSQL JDBC 13.2.1, Jakarta Persistence 3.1.0 trong `pom.xml`.
  - Cấu hình `src/main/resources/META-INF/persistence.xml` với `hsf302PU`.
- **TODO 2.1: Entity Department, Employee, Gender**
  - Khai báo đúng các kiểu dữ liệu: `BigDecimal salary`, `LocalDate hireDate`, `@Enumerated(EnumType.STRING) Gender`, `boolean active`.
  - `@Column(unique = true)` cho `name` (Department) và `email` (Employee).
- **TODO 2.2: Owning side trong Employee**
  - `@ManyToOne(fetch = FetchType.LAZY)` + `@JoinColumn(name = "department_id", nullable = false)`.
- **TODO 2.3: Inverse side trong Department**
  - `@OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)`.
  - Khởi tạo sẵn `new ArrayList<>()`.
- **TODO 2.4: Helper method đồng bộ 2 chiều**
  - `addEmployee(Employee e)` và `removeEmployee(Employee e)` cập nhật cả 2 chiều đối tượng.
- **TODO 2.5: DAO cho Department và Employee**
  - Đầy đủ `save`, `findById`, `findAll`, `update`, `delete` với quản lý `EntityManager`, `EntityTransaction` và `try-catch-finally`.
- **TODO 2.6: JPQL JOIN FETCH**
  - `findByIdWithEmployees(int id)`: lấy Department và toàn bộ Employees chỉ bằng 1 câu truy vấn, không ném ngoại lệ dù `EntityManager` đã đóng.
- **TODO 2.7: Demo trong Main**
  - Tạo 1 Department + 3 Employee bằng `addEmployee()`, gọi `save(department)` (cascade tự lưu nhân viên), tìm lại bằng `findByIdWithEmployees()`.
- **TODO 2.8: Tái hiện N+1 Query Problem**
  - Gọi `findAll()` các Department không dùng JOIN FETCH, loop qua `getEmployees()` gây ra **1 + N câu SQL**.
- **TODO 2.9: Fix N+1 bằng JOIN FETCH**
  - Viết `findAllWithEmployees()` dùng `JOIN FETCH`: chỉ sinh **duy nhất 1 câu SQL** cho toàn bộ dữ liệu.

---

## 4. Cách chạy chương trình

### Cách 1: Chạy trực tiếp từ IntelliJ IDEA
- Mở file `src/main/java/fu/se123456/Main.java`, click nút **Run** (biểu tượng mũi tên xanh).
- Chạy unit test: Mở file `src/test/java/fu/se123456/JPAOneToManyTest.java` và click **Run All Tests**.

### Cách 2: Chạy bằng Maven CLI
```bash
mvn clean compile exec:java -Dexec.mainClass="fu.se123456.Main"
```
Chạy kiểm thử:
```bash
mvn test
```
