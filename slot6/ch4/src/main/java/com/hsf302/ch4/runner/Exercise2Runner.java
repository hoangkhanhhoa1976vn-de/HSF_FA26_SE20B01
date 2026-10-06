package com.hsf302.ch4.runner;

import com.hsf302.ch4.pojo.Course;
import org.springframework.data.domain.Page;
import com.hsf302.ch4.pojo.Student;
import org.hibernate.LazyInitializationException;
import com.hsf302.ch4.service.CourseService;
import com.hsf302.ch4.service.EnrollmentService;
import com.hsf302.ch4.service.StudentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
@Order(3)
@Profile("ex2")
public class Exercise2Runner implements CommandLineRunner {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;

    public Exercise2Runner(CourseService courseService, EnrollmentService enrollmentService, StudentService studentService) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
    }

    @Override
    public void run(String... args) {
        partB();
        partC();
        partD();
        bonus();
        partE();
    }

    private void partB() {
        todo6();
        todo7();
    }
    private void partC() {
        todo8();
        todo9();
        todo10();
        todo11();
    }
    private void partD() {
        todo12();
        todo13();
        todo14();
        todo15();
        todo16();
        todo17();
        todo18();
        todo19();
    }
    private void bonus() { }
    private void partE() {
        todo20();
        todo21();
        todo22();
        todo23();
        testTeacherQuestions();
    }

    private void title(String t) {
        System.out.println("\n===== " + t + " =====");
    }

    private void printList(String label, Collection<?> list) {
        System.out.println("-- " + label + ":");
        list.forEach(o -> System.out.println("   " + o));
        System.out.println("   -> " + list.size() + " record(s)");
    }

    private void attempt(String label, Runnable action) {
        try {
            action.run();
            System.out.println("   [OK]   " + label);
        } catch (RuntimeException e) {
            System.out.println("   [FAIL] " + label + " -> " + e.getMessage());
        }
    }

    private void todo6() {
        title("TODO 6: count, findAll(Sort), findById");
        System.out.println("Total courses: " + courseService.count());
        printList("All courses order by code", courseService.findAllOrderByCode());
        for (long id : new long[]{2L, 99L}) {
            System.out.println("findById(" + id + "): "
                    + courseService.findById(id).map(Course::toString).orElse("Not found"));
        }
    }

    private void todo7() {
        title("TODO 7: navigate student.getCourses() / course.getStudents()");
        printList("(a) Courses of SE001", enrollmentService.getCoursesOfStudent("SE001"));
        printList("(b) Students of AIL303", enrollmentService.getStudentsOfCourse("AIL303"));
    }

    private void todo8() {
        title("TODO 8: findByCode, findBySemester, countBySemester");
        for (String code : List.of("HSF302", "XXX000")) {
            System.out.println("(a) " + code + ": "
                    + courseService.findByCode(code).map(Course::getName).orElse("Not found"));
        }
        printList("(b) Semester SU26", courseService.findBySemester("SU26"));
        System.out.println("(c) Courses in FA26: " + courseService.countBySemester("FA26"));
    }

    private void todo9() {
        title("TODO 9: derived query through collection courses");
        printList("(a) Students of PRJ301", enrollmentService.findStudentsInCourse("PRJ301"));
        System.out.println("(b) Students of HSF302: " + enrollmentService.countStudentsInCourse("HSF302"));
        printList("(c) Active students of PRJ301", enrollmentService.findActiveStudentsInCourse("PRJ301"));
    }

    private void todo10() {
        title("TODO 10: derived query from inverse side, Distinct");
        printList("(a) Courses of SE002", courseService.findCoursesOfStudent("SE002"));
        printList("(b1) Courses of AI students - no Distinct", courseService.findCoursesOfDepartment("AI", false));
        printList("(b2) Courses of AI students - Distinct", courseService.findCoursesOfDepartment("AI", true));
    }

    private void todo11() {
        title("TODO 11: IsEmpty, existsBy...And...");
        printList("(a) Students without courses", enrollmentService.findStudentsWithoutCourses());
        printList("(b) Courses without students", courseService.findCoursesWithoutStudents());
        System.out.println("(c) SE001 enrolled AIL303? " + enrollmentService.isEnrolled("SE001", "AIL303"));
        System.out.println("    SE002 enrolled AIL303? " + enrollmentService.isEnrolled("SE002", "AIL303"));
    }

    private void todo12() {
        title("TODO 12: JPQL JOIN s.courses");
        printList("HSF302 & GPA >= 3.5", enrollmentService.findGoodStudentsInCourse("HSF302", 3.5));
    }

    private void todo13() {
        title("TODO 13: course statistics (LEFT JOIN + GROUP BY + DTO)");
        printCourseStats();
    }

    private void printCourseStats() {
        courseService.getStatistics().forEach(d -> System.out.printf(
                "   %-6s | %-40s | %d/%d (free %d) | avg GPA %s%n",
                d.code(), d.name(), d.enrolled(), d.capacity(), d.remaining(),
                d.avgGpa() == null ? "null" : String.format("%.3f", d.avgGpa())));
    }

    private void todo14() {
        title("TODO 14: total credits per student (GROUP BY + HAVING)");
        enrollmentService.getCreditSummary(7).forEach(d -> System.out.printf(
                "   %s | %-15s | %d course(s) | %d credits%n",
                d.studentCode(), d.fullName(), d.courseCount(), d.totalCredits()));
    }

    private void todo15() {
        title("TODO 15: SIZE() on collections");
        printList("(a) Full courses", courseService.findFullCourses());
        printList("(b) Students with more than 2 courses", enrollmentService.findStudentsWithMoreThan(2));
    }

    private void todo16() {
        title("TODO 16: LazyInitializationException, JOIN FETCH, @EntityGraph");

        try {
            Student s = studentService.findByStudentCode("SE001").orElseThrow();
            System.out.println("(a) courses = " + s.getCourses().size());
        } catch (LazyInitializationException e) {
            System.out.println("(a) Caught: " + e.getClass().getSimpleName());
            System.out.println("    " + e.getMessage());
        }

        Student s = enrollmentService.getStudentWithCourses("SE001");
        System.out.println("(b) " + s.getStudentCode() + " - " + s.getFullName());
        s.getCourses().stream()
                .sorted(Comparator.comparing(Course::getCode))
                .forEach(c -> System.out.println("   " + c));

        Course c = courseService.getWithStudents("SWP391");
        System.out.println("(c) " + c.getCode() + " - " + c.getName());
        c.getStudents().stream()
                .sorted(Comparator.comparing(Student::getFullName))
                .forEach(st -> System.out.println("   " + st));
    }

    private void todo17() {
        title("TODO 17: native SQL on join table - top 3 enrolled courses");
        courseService.findTopEnrolled(3).forEach(r -> System.out.printf(
                "   %s | %-35s | %d student(s)%n", r.getCode(), r.getName(), r.getEnrolled()));
    }

    private void todo18() {
        title("TODO 18: interface projection - enrollments of department AI");
        enrollmentService.getEnrollmentsOfDepartment("AI").forEach(v -> System.out.printf(
                "   %s | %-14s | %s | %-35s | %d%n",
                v.getStudentCode(), v.getFullName(), v.getCourseCode(), v.getCourseName(), v.getCredits()));
    }

    private void todo19() {
        title("TODO 19: paginate students of HSF302 (size 2, order by fullName)");
        int pageIndex = 0;
        Page<Student> page;
        do {
            page = enrollmentService.findStudentsInCoursePage("HSF302", pageIndex, 2);
            printList("Page " + pageIndex, page.getContent());
            pageIndex++;
        } while (page.hasNext());
        System.out.println("totalElements = " + page.getTotalElements()
                + ", totalPages = " + page.getTotalPages());
    }

    private void todo20() {
        title("TODO 20: enroll with business rules");
        attempt("enroll IA003 -> MKT101", () -> enrollmentService.enroll("IA003", "MKT101"));
        attempt("enroll SE001 -> PRJ301", () -> enrollmentService.enroll("SE001", "PRJ301"));
        attempt("enroll SE004 -> AIL303", () -> enrollmentService.enroll("SE004", "AIL303"));
        attempt("enroll SE003 -> HSF302", () -> enrollmentService.enroll("SE003", "HSF302"));
        attempt("enroll XX999 -> HSF302", () -> enrollmentService.enroll("XX999", "HSF302"));
        printList("Courses of IA003", enrollmentService.getCoursesOfStudent("IA003"));
        System.out.println("Students of MKT101: " + enrollmentService.countStudentsInCourse("MKT101"));
    }

    private void todo21() {
        title("TODO 21: unenroll");
        attempt("unenroll AI002 <- AIL303", () -> enrollmentService.unenroll("AI002", "AIL303"));
        attempt("unenroll IA003 <- PRJ301", () -> enrollmentService.unenroll("IA003", "PRJ301"));
        attempt("enroll   SE004 -> AIL303", () -> enrollmentService.enroll("SE004", "AIL303"));
        printList("Students of AIL303", enrollmentService.getStudentsOfCourse("AIL303"));
        printList("Courses of AI002", enrollmentService.getCoursesOfStudent("AI002"));
        System.out.println("AI002 still exists? " + studentService.findByStudentCode("AI002").isPresent());
        System.out.println("Total courses: " + courseService.count());
    }

    private void todo22() {
        title("TODO 22: switch course in one transaction");
        attempt("switch SE001 SWP391 -> MKT101",
                () -> enrollmentService.switchCourse("SE001", "SWP391", "MKT101"));
        printList("Courses of SE001", enrollmentService.getCoursesOfStudent("SE001"));

        attempt("switch SE001 PRJ301 -> AIL303",
                () -> enrollmentService.switchCourse("SE001", "PRJ301", "AIL303"));
        printList("Courses of SE001 (after rollback)", enrollmentService.getCoursesOfStudent("SE001"));
    }

    private void todo23() {
        title("TODO 23: delete course");
        try {
            courseService.deleteCourseDirectly("IAA202");
            System.out.println("(a) Deleted ?!");
        } catch (DataIntegrityViolationException e) {
            System.out.println("(a) Caught: " + e.getClass().getSimpleName());
            System.out.println("    " + e.getMostSpecificCause().getMessage());
        }

        System.out.println("(b) Unlinked students: " + courseService.deleteCourse("IAA202"));
        printList("Remaining courses", courseService.findAllOrderByCode());
        printList("Courses of IA002", enrollmentService.getCoursesOfStudent("IA002"));
    }

    private void testTeacherQuestions() {
        title("KIỂM TRA CÔ BÍCH TRA");
        System.out.println("-- Câu 1a (Derived method: credits [2, 3]):");
        courseService.findByCreditsRange(2, 3, false).forEach(c -> System.out.println("   " + c));

        System.out.println("-- Câu 1b (Custom query JPQL: credits [2, 3]):");
        courseService.findByCreditsRange(2, 3, true).forEach(c -> System.out.println("   " + c));

        System.out.println("-- Câu 2a (Derived method: đếm credits > 3): "
                + courseService.countCoursesWithCreditsGreaterThan(3, false));
        System.out.println("-- Câu 2b (Custom query JPQL: đếm credits > 3): "
                + courseService.countCoursesWithCreditsGreaterThan(3, true));

        System.out.println("-- Câu 3 (Custom query JPQL: tìm khóa có tên chứa 'ing' không phân biệt hoa thường):");
        courseService.searchByName("ing").forEach(c -> System.out.println("   " + c));

        System.out.println("-- Câu 4 (Thống kê theo học kỳ: số khóa, số lượt đăng ký):");
        courseService.getSemesterStats().forEach(s -> System.out.printf(
                "   Học kỳ: %-5s | Số khóa: %d | Số lượt ĐK: %d%n",
                s.semester(), s.courseCount(), s.totalEnrollments()));

        System.out.println("-- Câu 5 (Native Query: tổng tín chỉ đã đăng ký theo khoa, giảm dần):");
        courseService.getDepartmentCreditsNative().forEach(d -> System.out.printf(
                "   Khoa: %-4s (%-25s) | Tổng tín chỉ: %d%n",
                d.getDepartmentCode(), d.getDepartmentName(), d.getTotalCredits()));
    }
}