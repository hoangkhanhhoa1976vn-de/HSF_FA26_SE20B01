package com.hsf302.ch4.service;

import com.hsf302.ch4.dto.CourseEnrollmentCount;
import com.hsf302.ch4.dto.CourseStatDTO;
import com.hsf302.ch4.dto.DepartmentCreditStat;
import com.hsf302.ch4.dto.SemesterStatDTO;
import com.hsf302.ch4.pojo.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    long count();
    List<Course> findAllOrderByCode();
    Optional<Course> findById(Long id);
    Optional<Course> findByCode(String code);
    List<Course> findBySemester(String semester);
    long countBySemester(String semester);

    List<Course> findCoursesOfStudent(String studentCode);
    List<Course> findCoursesOfDepartment(String deptCode, boolean distinct);
    List<Course> findCoursesWithoutStudents();
    List<CourseStatDTO> getStatistics();
    List<Course> findFullCourses();
    Course getWithStudents(String code);
    List<CourseEnrollmentCount> findTopEnrolled(int n);
    void deleteCourseDirectly(String code);
    int deleteCourse(String code);

    // ===== Kiểm tra theo yêu cầu cô Bích Tra =====
    List<Course> findByCreditsRange(int min, int max, boolean useCustomQuery);
    long countCoursesWithCreditsGreaterThan(int credits, boolean useCustomQuery);
    List<Course> searchByName(String keyword);
    List<SemesterStatDTO> getSemesterStats();
    List<DepartmentCreditStat> getDepartmentCreditsNative();
}