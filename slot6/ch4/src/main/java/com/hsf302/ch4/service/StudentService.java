package com.hsf302.ch4.service;

import com.hsf302.ch4.dto.StudentSummary;
import com.hsf302.ch4.pojo.Gender;
import com.hsf302.ch4.pojo.Student;
import org.springframework.data.domain.Page;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentService {
    long count();
    Optional<Student> findById(Long id);
    List<Student> findAllOrderByGpaDesc();
    Page<Student> findPage(int pageIndex, int size, String sortField);
    Optional<Student> findByStudentCode(String studentCode);
    boolean isEmailExisted(String email);
    long countActive();
    List<Student> searchByName(String keyword);
    List<Student> findByEmailDomain(String domain);
    List<Student> findWithoutEmail();
    List<Student> findByGpaRange(double min, double max);
    List<Student> findActiveByGender(Gender gender);
    List<Student> findBornAfter(LocalDate date);
    List<Student> findByDepartment(String deptCode);
    long countByDepartment(String deptCode);
    List<Student> findTop3ByGpa();
    List<Student> findGoodStudents(String deptCode, double minGpa);
    List<Student> searchByKeyword(String keyword);
    List<Student> findAboveAverageGpa();
    List<Student> findTopNInDepartment(String deptCode, int n);
    List<StudentSummary> getActiveSummaries();
    Page<Student> findActiveByDepartment(String deptCode, int pageIndex, int size);
    List<Student> search(String kw, String deptCode, Double minGpa, Boolean active);
    Student updateGpa(String studentCode, double newGpa);
    int deactivateLowGpa(double threshold);
    long deleteInactiveStudents();
}
