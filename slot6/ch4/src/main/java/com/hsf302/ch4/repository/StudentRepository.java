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
    Optional<Student> findByStudentCode(String code);                           // TODO 8, 20
    boolean existsByEmail(String email);                                        // TODO 8
    long countByActiveTrue();                                                   // TODO 8, 21

    List<Student> findByFullNameContainingIgnoreCase(String kw);                // TODO 9
    List<Student> findByEmailEndingWith(String suffix);                         // TODO 9
    List<Student> findByEmailIsNull();                                          // TODO 9

    List<Student> findByGpaBetweenOrderByGpaDesc(double min, double max);       // TODO 10
    List<Student> findByGenderAndActiveTrue(Gender gender);                     // TODO 10
    List<Student> findByDobAfter(LocalDate date);                               // TODO 10

    List<Student> findByDepartment_CodeOrderByFullNameAsc(String code);         // TODO 11
    long countByDepartment_Code(String code);                                   // TODO 11, 22
    List<Student> findTop3ByOrderByGpaDesc();                                   // TODO 11

    // ===== Part D - Custom query with @Query =====
    @Query("SELECT s FROM Student s WHERE s.department.code = :code AND s.gpa >= :minGpa ORDER BY s.gpa DESC")
    List<Student> findGoodStudentsInDepartment(@Param("code") String code, @Param("minGpa") double minGpa); // TODO 12

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
           "(s.email IS NOT NULL AND LOWER(s.email) LIKE LOWER(CONCAT('%', :kw, '%'))) " +
           "ORDER BY s.fullName ASC")
    List<Student> searchByKeyword(@Param("kw") String keyword);                 // TODO 13

    @Query("SELECT s FROM Student s WHERE s.gpa > (SELECT AVG(s2.gpa) FROM Student s2) ORDER BY s.gpa DESC")
    List<Student> findAboveAverageGpa();                                        // TODO 15

    @Query(value = "SELECT TOP (:n) s.* FROM students s " +
                   "JOIN departments d ON d.id = s.department_id " +
                   "WHERE d.code = :code ORDER BY s.gpa DESC",
           nativeQuery = true)
    List<Student> findTopNByDepartmentNative(@Param("code") String code, @Param("n") int n); // TODO 17

    @Query("SELECT s.studentCode AS studentCode, s.fullName AS fullName, s.gpa AS gpa, " +
           "d.name AS departmentName " +
           "FROM Student s JOIN s.department d " +
           "WHERE s.active = true ORDER BY s.fullName ASC")
    List<StudentSummary> findActiveSummaries();                                  // TODO 18

    @Query("SELECT s FROM Student s WHERE s.department.code = :code AND s.active = true")
    Page<Student> findActiveByDepartment(@Param("code") String code, Pageable pageable); // TODO 19

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Student s SET s.active = false WHERE s.gpa < :threshold AND s.active = true")
    int deactivateLowGpa(@Param("threshold") double threshold);                 // TODO 21

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Student s SET s.department = :to WHERE s.department = :from")
    int transferStudents(@Param("from") Department from, @Param("to") Department to); // TODO 22

    long deleteByActiveFalse();                                                 // TODO 23
}
