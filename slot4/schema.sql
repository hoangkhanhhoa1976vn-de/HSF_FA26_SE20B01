-- ==========================================================
-- SCRIPT TAO DATABASE VA BANG CHO BAI THUC HANH:
-- CHAPTER 1: JPA MAPPING MANYTOMANY (SLOT 4)
-- ==========================================================

-- 1. Tao Co So Du Lieu neu chua ton tai
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'HSF302_ManyToMany')
BEGIN
    CREATE DATABASE HSF302_ManyToMany;
END
GO

USE HSF302_ManyToMany;
GO

-- 2. Cap quyen cho User 'employee_user' (neu co)
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'employee_user')
BEGIN
    IF EXISTS (SELECT * FROM sys.server_principals WHERE name = 'employee_user')
    BEGIN
        CREATE USER employee_user FOR LOGIN employee_user;
        ALTER ROLE db_owner ADD MEMBER employee_user;
    END
END
GO

-- 3. Tao bang employees (Nhan vien) - Owning side quan he N-N
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'employees')
BEGIN
    CREATE TABLE employees (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        full_name NVARCHAR(255),
        salary NUMERIC(18,2),
        hire_date DATE,
        email NVARCHAR(255) NOT NULL UNIQUE,
        gender VARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
        active BIT NOT NULL DEFAULT 1
    );
END
GO

-- 4. Tao bang projects (Du an) - Inverse side quan he N-N
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'projects')
BEGIN
    CREATE TABLE projects (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        project_code NVARCHAR(100) NOT NULL UNIQUE,
        project_name NVARCHAR(255) NOT NULL,
        budget NUMERIC(18,2),
        start_date DATE,
        end_date DATE
    );
END
GO

-- 5. Tao bang trung gian employee_project (ManyToMany Join Table)
-- Hibernate tu dong tao voi 2 cot FK (employee_id, project_id) lam composite primary key
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'employee_project')
BEGIN
    CREATE TABLE employee_project (
        employee_id BIGINT NOT NULL,
        project_id BIGINT NOT NULL,
        PRIMARY KEY (employee_id, project_id),
        CONSTRAINT FK_employee_project_employee FOREIGN KEY (employee_id) 
            REFERENCES employees(id) ON DELETE NO ACTION,
        CONSTRAINT FK_employee_project_project FOREIGN KEY (project_id) 
            REFERENCES projects(id) ON DELETE NO ACTION
    );
END
GO

-- ==========================================================
-- GHI CHU QUAN TRONG:
-- 1. Trong file persistence.xml, cau hinh:
--    <property name="hibernate.hbm2ddl.auto" value="update"/>
--    Hibernate se TU DONG tao/cap nhat cac bang 'employees', 'projects',
--    va bang trung gian 'employee_project' khi chay ung dung, ban khong
--    nhat thiet phai chay script nay thu cong!
-- 2. Neu ban muon chay thu cong tren SQL Server Management Studio (SSMS):
--    Chi can mo file nay len SSMS va nhan Execute (F5).
-- ==========================================================
