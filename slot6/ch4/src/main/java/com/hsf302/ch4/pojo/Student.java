package com.hsf302.ch4.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_code", nullable = false, unique = true, length = 10)
    private String studentCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    private LocalDate dob;

    private Double gpa;

    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // Owning side: Student quản lý bảng trung gian student_courses
    @ManyToMany
    @JoinTable(
        name = "student_courses",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();

    // ===== Helper đồng bộ 2 chiều =====
    public void enroll(Course c) {
        courses.add(c);
        c.getStudents().add(this);
    }

    public void unenroll(Course c) {
        courses.remove(c);
        c.getStudents().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student other)) return false;
        return studentCode != null && studentCode.equals(other.getStudentCode());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(studentCode);
    }

    @Override
    public String toString() {
        return String.format("%s | %-15s | %-20s | %.1f | %s",
                studentCode, fullName, email, gpa, active ? "active" : "inactive");
    }
}