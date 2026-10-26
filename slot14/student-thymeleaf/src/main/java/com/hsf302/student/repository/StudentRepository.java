package com.hsf302.student.repository;

import com.hsf302.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByStudentCodeContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrderByIdAsc(String code, String name);

    boolean existsByStudentCodeIgnoreCase(String studentCode);

    boolean existsByStudentCodeIgnoreCaseAndIdNot(String studentCode, Long id);
}
