-- ==========================================================
-- SCRIPT TAO DATABASE VA BANG CHO BAI THUC HANH 2:
-- CRUD JPA MAPPING (OneToMany / ManyToOne)
-- ==========================================================

-- 1. Tao co so du lieu
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'HSF302_Chapter1')
BEGIN
    CREATE DATABASE HSF302_Chapter1;
END
GO

USE HSF302_Chapter1;
GO

-- 2. Tao bang departments (Phong ban)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'departments')
BEGIN
    CREATE TABLE departments (
        id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(255) NOT NULL UNIQUE,
        location NVARCHAR(255)
    );
END
GO

-- 3. Tao bang employees (Nhan vien)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'employees')
BEGIN
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
END
GO

-- ==========================================================
-- LUU Y: Khi chay ung dung JPA/Hibernate voi cau hinh:
--   <property name="hibernate.hbm2ddl.auto" value="update"/>
-- Hibernate se tu dong tao cac bang va rang buoc tren ma khong
-- can chay script nay thu cong!
-- ==========================================================
