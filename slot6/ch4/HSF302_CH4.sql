IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'HSF302_CH4')
BEGIN
    CREATE DATABASE HSF302_CH4;
END
GO

USE HSF302_CH4;
GO

IF OBJECT_ID('dbo.students', 'U') IS NOT NULL DROP TABLE dbo.students;
IF OBJECT_ID('dbo.departments', 'U') IS NOT NULL DROP TABLE dbo.departments;
GO

CREATE TABLE dbo.departments (
    id BIGINT IDENTITY(1,1) NOT NULL,
    code VARCHAR(10) NOT NULL,
    name NVARCHAR(100) NOT NULL,
    CONSTRAINT PK_departments PRIMARY KEY (id),
    CONSTRAINT UQ_departments_code UNIQUE (code)
);
GO

CREATE TABLE dbo.students (
    id BIGINT IDENTITY(1,1) NOT NULL,
    student_code VARCHAR(10) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) NULL,
    gender VARCHAR(10) NULL,
    dob DATE NULL,
    gpa FLOAT NULL,
    active BIT NOT NULL,
    department_id BIGINT NOT NULL,
    CONSTRAINT PK_students PRIMARY KEY (id),
    CONSTRAINT UQ_students_student_code UNIQUE (student_code),
    CONSTRAINT UQ_students_email UNIQUE (email),
    CONSTRAINT FK_students_departments FOREIGN KEY (department_id) 
        REFERENCES dbo.departments(id)
);
GO

SET IDENTITY_INSERT dbo.departments ON;
INSERT INTO dbo.departments (id, code, name) VALUES
(1, 'SE', N'Software Engineering'),
(2, 'AI', N'Artificial Intelligence'),
(3, 'IA', N'Information Assurance'),
(4, 'GD', N'Graphic Design');
SET IDENTITY_INSERT dbo.departments OFF;
GO

SET IDENTITY_INSERT dbo.students ON;
INSERT INTO dbo.students (id, student_code, full_name, email, gender, dob, gpa, active, department_id) VALUES
(1,  'SE001', N'Nguyen Van An',   'an.nv@fpt.edu.vn',    'MALE',   '2005-03-15', 3.2, 1, 1),
(2,  'SE002', N'Tran Thi Binh',   'binh.tt@fpt.edu.vn',  'FEMALE', '2004-07-22', 3.8, 1, 1),
(3,  'SE003', N'Le Van Cuong',    'cuong.lv@fpt.edu.vn', 'MALE',   '2003-11-05', 2.5, 0, 1),
(4,  'AI001', N'Pham Thi Dung',   'dung.pt@fpt.edu.vn',  'FEMALE', '2006-01-10', 3.5, 1, 2),
(5,  'AI002', N'Hoang Van Em',    'em.hv@gmail.com',     'MALE',   '2002-09-30', 2.8, 1, 2),
(6,  'AI003', N'Vo Thi Hoa',      'hoa.vt@fpt.edu.vn',   'FEMALE', '2005-05-18', 3.9, 1, 2),
(7,  'IA001', N'Dang Van Giang',  'giang.dv@gmail.com',  'MALE',   '2001-12-01', 1.9, 0, 3),
(8,  'IA002', N'Bui Thi Lan',     'lan.bt@fpt.edu.vn',   'FEMALE', '2004-02-14', 3.1, 1, 3),
(9,  'SE004', N'Nguyen Thi Mai',  'mai.nt@fpt.edu.vn',   'FEMALE', '2003-08-08', 3.6, 1, 1),
(10, 'IA003', N'Do Van Nam',      NULL,                  'MALE',   '2005-10-20', 2.2, 1, 3);
SET IDENTITY_INSERT dbo.students OFF;
GO

SELECT * FROM dbo.departments;
SELECT * FROM dbo.students;
GO
