package com.hsf302.ch4.runner;

import com.hsf302.ch4.service.DepartmentService;
import com.hsf302.ch4.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
    }
    private void partC() {}
    private void partD() {}
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
