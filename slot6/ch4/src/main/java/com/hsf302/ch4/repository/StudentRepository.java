package com.hsf302.ch4.repository;

import com.hsf302.ch4.pojo.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long>,
                                           JpaSpecificationExecutor<Student> {
    Optional<Student> findByStudentCode(String code);                           // TODO 8, 20
    boolean existsByEmail(String email);                                        // TODO 8
    long countByActiveTrue();                                                   // TODO 8, 21

    List<Student> findByFullNameContainingIgnoreCase(String kw);                // TODO 9
    List<Student> findByEmailEndingWith(String suffix);                         // TODO 9
    List<Student> findByEmailIsNull();                                          // TODO 9
}
