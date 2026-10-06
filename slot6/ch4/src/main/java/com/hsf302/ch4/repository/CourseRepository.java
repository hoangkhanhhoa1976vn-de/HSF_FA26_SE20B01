package com.hsf302.ch4.repository;

import com.hsf302.ch4.dto.CourseEnrollmentCount;
import com.hsf302.ch4.dto.CourseStatDTO;
import com.hsf302.ch4.dto.DepartmentCreditStat;
import com.hsf302.ch4.dto.SemesterStatDTO;
import org.springframework.data.repository.query.Param;
import com.hsf302.ch4.pojo.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    List<Course> findBySemesterOrderByCodeAsc(String semester);
    long countBySemester(String semester);

    List<Course> findByStudents_StudentCodeOrderByCodeAsc(String studentCode);
    List<Course> findByStudents_Department_CodeOrderByCodeAsc(String deptCode);
    List<Course> findDistinctByStudents_Department_CodeOrderByCodeAsc(String deptCode);
    List<Course> findByStudentsIsEmpty();

    @Query("SELECT new com.hsf302.ch4.dto.CourseStatDTO(c.code, c.name, c.capacity, COUNT(s), AVG(s.gpa)) " +
           "FROM Course c LEFT JOIN c.students s " +
           "GROUP BY c.code, c.name, c.capacity ORDER BY c.code")
    List<CourseStatDTO> getCourseStats();

    @Query("SELECT c FROM Course c WHERE SIZE(c.students) >= c.capacity ORDER BY c.code")
    List<Course> findFullCourses();

    @EntityGraph(attributePaths = "students")
    Optional<Course> findWithStudentsByCode(String code);

    @Query(value = "SELECT TOP (:n) c.code AS code, c.name AS name, COUNT(sc.student_id) AS enrolled " +
                   "FROM courses c LEFT JOIN student_courses sc ON sc.course_id = c.id " +
                   "GROUP BY c.code, c.name " +
                   "ORDER BY enrolled DESC, c.code",
           nativeQuery = true)
    List<CourseEnrollmentCount> findTopEnrolledNative(@Param("n") int n);

    // ===== Câu hỏi kiểm tra cô Bích Tra =====
    // Câu 1a - Derived method: Khóa học có credits trong khoảng min-max
    List<Course> findByCreditsBetween(Integer min, Integer max);

    // Câu 1b - Custom query: Khóa học có credits trong khoảng min-max
    @Query("SELECT c FROM Course c WHERE c.credits BETWEEN :min AND :max ORDER BY c.credits ASC")
    List<Course> findCoursesByCreditsRange(@Param("min") int min, @Param("max") int max);

    // Câu 2a - Derived method: Đếm số khóa có credits > x
    long countByCreditsGreaterThan(Integer credits);

    // Câu 2b - Custom query: Đếm số khóa có credits > x
    @Query("SELECT COUNT(c) FROM Course c WHERE c.credits > :credits")
    long countCoursesWithCreditsGreaterThan(@Param("credits") int credits);

    // Câu 3 - Custom query: Tìm khóa có tên chứa từ khóa (không phân biệt hoa thường)
    @Query("SELECT c FROM Course c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :kw, '%')) ORDER BY c.name ASC")
    List<Course> searchByNameCustom(@Param("kw") String keyword);

    // Câu 3 (Derived method đối chiếu)
    List<Course> findByNameContainingIgnoreCase(String keyword);

    // Câu 4 - Thống kê theo học kỳ: số khóa, số lượt đăng ký
    @Query("SELECT new com.hsf302.ch4.dto.SemesterStatDTO(c.semester, COUNT(DISTINCT c), COUNT(s)) " +
           "FROM Course c LEFT JOIN c.students s " +
           "GROUP BY c.semester ORDER BY c.semester")
    List<SemesterStatDTO> getSemesterStats();

    // Câu 5 - Native Query: tổng tín chỉ đã đăng ký theo khoa (departments, students, student_courses, courses), sắp xếp giảm dần
    @Query(value = "SELECT d.code AS departmentCode, " +
                   "       d.name AS departmentName, " +
                   "       COALESCE(SUM(c.credits), 0) AS totalCredits " +
                   "FROM departments d " +
                   "LEFT JOIN students s ON s.department_id = d.id " +
                   "LEFT JOIN student_courses sc ON sc.student_id = s.id " +
                   "LEFT JOIN courses c ON c.id = sc.course_id " +
                   "GROUP BY d.code, d.name " +
                   "ORDER BY totalCredits DESC, d.code ASC",
           nativeQuery = true)
    List<DepartmentCreditStat> getDepartmentCreditsNative();
}