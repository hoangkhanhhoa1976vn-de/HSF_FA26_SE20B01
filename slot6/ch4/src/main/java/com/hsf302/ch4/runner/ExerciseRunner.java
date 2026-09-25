package com.hsf302.ch4.runner;

import com.hsf302.ch4.pojo.Department;
import com.hsf302.ch4.pojo.Gender;
import com.hsf302.ch4.pojo.Student;
import com.hsf302.ch4.service.DepartmentService;
import com.hsf302.ch4.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.hibernate.LazyInitializationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;

@Component
@Order(2)
@RequiredArgsConstructor
public class ExerciseRunner implements CommandLineRunner {

    private final DepartmentService departmentService;
    private final StudentService studentService;

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
    private void bonus() {
        todo24();
    }
    private void partE() {
        todo20();
        todo21();
        todo22();
        todo23();
    }

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

    private void todo15() {
        title("TODO 15: Subquery AVG");
        printList("Students with GPA > overall average", studentService.findAboveAverageGpa());
    }

    private void todo16() {
        title("TODO 16: LazyInitializationException & JOIN FETCH");

        System.out.println("-- 16a. Truy cập students ngoài transaction:");
        try {
            Department d = departmentService.findByCode("AI").orElseThrow();
            System.out.println("   Department: " + d);
            int size = d.getStudents().size();
            System.out.println("   Students size: " + size);
        } catch (LazyInitializationException e) {
            System.out.println("   [BẮT ĐƯỢC EXCEPTION MONG ĐỢI]: " + e.getClass().getSimpleName()
                    + " - " + e.getMessage());
        }

        System.out.println("-- 16b. Sửa bằng JOIN FETCH (1 câu SQL):");
        Department d = departmentService.getWithStudents("AI");
        System.out.println("   Department: " + d);
        printList("Students của " + d.getCode(), d.getStudents());
    }

    private void todo17() {
        title("TODO 17: Native SQL TOP (:n)");
        printList("Top 2 students of SE (native SQL)", studentService.findTopNInDepartment("SE", 2));
    }

    private void todo18() {
        title("TODO 18: Interface projection");
        System.out.println("-- Active student summaries:");
        studentService.getActiveSummaries().forEach(s -> System.out.println("   " + s.toDisplayString()));
    }

    private void todo19() {
        title("TODO 19: @Query + Pageable");
        for (int p = 0; p < 2; p++) {
            Page<Student> page = studentService.findActiveByDepartment("SE", p, 2);
            printList("Active SE students - Page " + p, page.getContent());
            System.out.printf("   [totalElements=%d, totalPages=%d, hasNext=%b]\n",
                    page.getTotalElements(), page.getTotalPages(), page.hasNext());
        }
    }

    private void todo24() {
        title("TODO 24: Specification (dynamic search)");
        printList("Search (null, 'AI', 3.0, true)", studentService.search(null, "AI", 3.0, true));
        printList("Search ('van', null, null, null)", studentService.search("van", null, null, null));
    }

    private void todo20() {
        title("TODO 20: Update GPA (dirty checking)");
        System.out.println("Before: " + studentService.findByStudentCode("SE001").orElseThrow());
        studentService.updateGpa("SE001", 3.4);
        System.out.println("After : " + studentService.findByStudentCode("SE001").orElseThrow());
    }

    private void todo21() {
        title("TODO 21: @Modifying UPDATE");
        int rows = studentService.deactivateLowGpa(2.5);
        System.out.println("Rows affected: " + rows);
        System.out.println("Active students now: " + studentService.countActive());
    }

    private void todo22() {
        title("TODO 22: Transfer students & delete department in 1 transaction");
        int moved = departmentService.transferStudentsAndDelete("IA", "SE");
        System.out.println("Students moved from IA to SE: " + moved);
        System.out.println("SE student count: " + studentService.countByDepartment("SE"));
        printList("Remaining departments", departmentService.findAll());
    }

    private void todo23() {
        title("TODO 23: Derived delete");
        long deleted = studentService.deleteInactiveStudents();
        System.out.println("Deleted: " + deleted);
        System.out.println("Students left: " + studentService.count());
        printList("Final statistics", departmentService.getStatistics());
    }

    private void title(String t) {
        System.out.println("\n===== " + t + " =====");
    }

    private void printList(String label, Collection<?> list) {
        System.out.println("-- " + label + ":");
        list.forEach(o -> System.out.println("   " + o));
        System.out.println("   -> " + list.size() + " record(s)");
    }
}
