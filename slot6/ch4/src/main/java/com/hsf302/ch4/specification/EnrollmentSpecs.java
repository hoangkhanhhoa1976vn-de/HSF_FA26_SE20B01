package com.hsf302.ch4.specification;

import com.hsf302.ch4.pojo.Course;
import com.hsf302.ch4.pojo.Student;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public final class EnrollmentSpecs {

    private EnrollmentSpecs() {
    }

    public static Specification<Student> enrolledIn(String courseCode) {
        return (root, query, cb) -> {
            if (courseCode == null || courseCode.isBlank()) return null;     // null = bỏ qua điều kiện
            query.distinct(true);                                          // JOIN collection → tránh trùng
            Join<Student, Course> c = root.join("courses");
            return cb.equal(c.get("code"), courseCode);
        };
    }

    public static Specification<Student> inSemester(String semester) {
        return (root, query, cb) -> {
            if (semester == null || semester.isBlank()) return null;
            query.distinct(true);
            Join<Student, Course> c = root.join("courses");
            return cb.equal(c.get("semester"), semester);
        };
    }

    public static Specification<Student> inDepartment(String deptCode) {
        return (root, query, cb) -> (deptCode == null || deptCode.isBlank())
                ? null
                : cb.equal(root.get("department").get("code"), deptCode);
    }

    public static Specification<Student> gpaAtLeast(Double minGpa) {
        return (root, query, cb) -> minGpa == null
                ? null
                : cb.greaterThanOrEqualTo(root.get("gpa"), minGpa);
    }
}
