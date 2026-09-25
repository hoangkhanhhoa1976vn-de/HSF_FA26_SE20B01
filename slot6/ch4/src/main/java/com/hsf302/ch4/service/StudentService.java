package com.hsf302.ch4.service;

import com.hsf302.ch4.pojo.Gender;
import com.hsf302.ch4.pojo.Student;
import org.springframework.data.domain.Page;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentService {
    long count();                                                                    // TODO 6
    Optional<Student> findById(Long id);                                             // TODO 6
    List<Student> findAllOrderByGpaDesc();                                           // TODO 7
    Page<Student> findPage(int pageIndex, int size, String sortField);               // TODO 7
    Optional<Student> findByStudentCode(String studentCode);                         // TODO 8
    boolean isEmailExisted(String email);                                            // TODO 8
    long countActive();                                                              // TODO 8, 21
    List<Student> searchByName(String keyword);                                      // TODO 9
    List<Student> findByEmailDomain(String domain);                                  // TODO 9
    List<Student> findWithoutEmail();                                                // TODO 9
    List<Student> findByGpaRange(double min, double max);                            // TODO 10
    List<Student> findActiveByGender(Gender gender);                                 // TODO 10
    List<Student> findBornAfter(LocalDate date);                                     // TODO 10
    List<Student> findByDepartment(String deptCode);                                 // TODO 11
    long countByDepartment(String deptCode);                                         // TODO 11, 22
    List<Student> findTop3ByGpa();                                                   // TODO 11
    List<Student> findGoodStudents(String deptCode, double minGpa);                  // TODO 12
    List<Student> searchByKeyword(String keyword);                                   // TODO 13
    List<Student> findAboveAverageGpa();                                             // TODO 15
    List<Student> findTopNInDepartment(String deptCode, int n);                      // TODO 17
}
