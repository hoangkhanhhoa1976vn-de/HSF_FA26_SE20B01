package com.hsf302.ch4.runner;

import com.hsf302.ch4.pojo.Gender;
import com.hsf302.ch4.pojo.Student;
import com.hsf302.ch4.service.DepartmentService;
import com.hsf302.ch4.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;

@Component
@Order(2)
@RequiredArgsConstructor
public class ExerciseRunner implements CommandLineRunner {

    // Runner CHỈ phụ thuộc vào Service (interface), KHÔNG inject Repository
    private final DepartmentService departmentService;
    private final StudentService studentService;

    @Override
    public void run(String... args) {
        partB();
        partC();
        partD();
        bonus();      // chạy trên dữ liệu gốc -> trước Part E
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
    }
    private void bonus() {}
    private void partE() {}

    private void todo6() {
        title("TODO 6: count(), findById(), existsById()");
        System.out.println("Departments: " + departmentService.count());
        System.out.println("Students   : " + studentService.count());
        System.out.println("Student id=1 : " + studentService.findById(1L).map(Object::toString).orElse("Not found"));
        System.out.println("Student id=99: " + studentService.findById(99L).map(Object::toString).orElse("Not found"));
        System.out.println("Department id=4 exists: " + departmentService.existsById(4L));
    }

    private void todo7() {
        title("TODO 7: findAll(Sort) & findAll(Pageable)");
        printList("All students sorted by GPA desc", studentService.findAllOrderByGpaDesc());

        Page<Student> page = studentService.findPage(1, 3, "fullName");
        printList("Page 1 (size=3, sort=fullName asc)", page.getContent());
        System.out.printf("   [totalElements=%d, totalPages=%d, hasNext=%b]\n",
                page.getTotalElements(), page.getTotalPages(), page.hasNext());
    }

    private void todo8() {
        title("TODO 8: findByStudentCode, existsByEmail, countByActiveTrue");
        System.out.println("AI002: " + studentService.findByStudentCode("AI002").map(Object::toString).orElse("Not found"));
        System.out.println("XX999: " + studentService.findByStudentCode("XX999").map(Object::toString).orElse("Not found"));
        System.out.println("Email 'binh.tt@fpt.edu.vn' exists: " + studentService.isEmailExisted("binh.tt@fpt.edu.vn"));
        System.out.println("Email 'nonexistent@fpt.edu.vn' exists: " + studentService.isEmailExisted("nonexistent@fpt.edu.vn"));
        System.out.println("Active students: " + studentService.countActive());
    }

    private void todo9() {
        title("TODO 9: searchByName, findByEmailDomain, findWithoutEmail");
        printList("Name containing 'nguyen' (ignore case)", studentService.searchByName("nguyen"));
        printList("Email ending with 'gmail.com'", studentService.findByEmailDomain("gmail.com"));
        printList("Students without email", studentService.findWithoutEmail());
    }

    private void todo10() {
        title("TODO 10: findByGpaRange, findActiveByGender, findBornAfter");
        printList("GPA in [3.0, 3.6] (sorted desc)", studentService.findByGpaRange(3.0, 3.6));
        printList("Active male students", studentService.findActiveByGender(Gender.MALE));
        printList("Born after 2005-01-01", studentService.findBornAfter(LocalDate.of(2005, 1, 1)));
    }

    private void todo11() {
        title("TODO 11: nested property, Top3, IsEmpty");
        printList("Students in SE dept (sorted by name)", studentService.findByDepartment("SE"));
        System.out.println("AI student count: " + studentService.countByDepartment("AI"));
        printList("Top 3 students by GPA", studentService.findTop3ByGpa());
        printList("Departments without students", departmentService.findDepartmentsWithoutStudents());
    }

    private void todo12() {
        title("TODO 12: JPQL named parameter");
        printList("Good students in SE (GPA >= 3.0)", studentService.findGoodStudents("SE", 3.0));
    }

    private void todo13() {
        title("TODO 13: JPQL LIKE + CONCAT + LOWER");
        printList("Keyword 'hoa'", studentService.searchByKeyword("hoa"));
        printList("Keyword 'gmail'", studentService.searchByKeyword("gmail"));
    }

    private void todo14() {
        title("TODO 14: Department statistics (DTO constructor expression)");
        printList("Department statistics", departmentService.getStatistics());
    }

    // ===== helpers =====
    private void title(String t) {
        System.out.println("\n===== " + t + " =====");
    }

    private void printList(String label, Collection<?> list) {
        System.out.println("-- " + label + ":");
        list.forEach(o -> System.out.println("   " + o));
        System.out.println("   -> " + list.size() + " record(s)");
    }
}
