package com.hsf302.ch4.service;

import com.hsf302.ch4.pojo.Student;
import org.springframework.data.domain.Page;
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
}
