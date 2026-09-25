package com.hsf302.ch4.repository;

import com.hsf302.ch4.dto.StudentSummary;
import com.hsf302.ch4.pojo.Department;
import com.hsf302.ch4.pojo.Gender;
import com.hsf302.ch4.pojo.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long>,
                                           JpaSpecificationExecutor<Student> {
    Optional<Student> findByStudentCode(String code);
    boolean existsByEmail(String email);
    long countByActiveTrue();

    List<Student> findByFullNameContainingIgnoreCase(String kw);
    List<Student> findByEmailEndingWith(String suffix);
    List<Student> findByEmailIsNull();

    List<Student> findByGpaBetweenOrderByGpaDesc(double min, double max);
    List<Student> findByGenderAndActiveTrue(Gender gender);
    List<Student> findByDobAfter(LocalDate date);

    List<Student> findByDepartment_CodeOrderByFullNameAsc(String code);
    long countByDepartment_Code(String code);
    List<Student> findTop3ByOrderByGpaDesc();

    @Query("SELECT s FROM Student s WHERE s.department.code = :code AND s.gpa >= :minGpa ORDER BY s.gpa DESC")
    List<Student> findGoodStudentsInDepartment(@Param("code") String code, @Param("minGpa") double minGpa);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
           "(s.email IS NOT NULL AND LOWER(s.email) LIKE LOWER(CONCAT('%', :kw, '%'))) " +
           "ORDER BY s.fullName ASC")
    List<Student> searchByKeyword(@Param("kw") String keyword);

    @Query("SELECT s FROM Student s WHERE s.gpa > (SELECT AVG(s2.gpa) FROM Student s2) ORDER BY s.gpa DESC")
    List<Student> findAboveAverageGpa();

    @Query(value = "SELECT TOP (:n) s.* FROM students s " +
                   "JOIN departments d ON d.id = s.department_id " +
                   "WHERE d.code = :code ORDER BY s.gpa DESC",
           nativeQuery = true)
    List<Student> findTopNByDepartmentNative(@Param("code") String code, @Param("n") int n);

    @Query("SELECT s.studentCode AS studentCode, s.fullName AS fullName, s.gpa AS gpa, " +
           "d.name AS departmentName " +
           "FROM Student s JOIN s.department d " +
           "WHERE s.active = true ORDER BY s.fullName ASC")
    List<StudentSummary> findActiveSummaries();

    @Query("SELECT s FROM Student s WHERE s.department.code = :code AND s.active = true")
    Page<Student> findActiveByDepartment(@Param("code") String code, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Student s SET s.active = false WHERE s.gpa < :threshold AND s.active = true")
    int deactivateLowGpa(@Param("threshold") double threshold);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Student s SET s.department = :to WHERE s.department = :from")
    int transferStudents(@Param("from") Department from, @Param("to") Department to);

    long deleteByActiveFalse();
}
