package com.hsf302.ch4.service;

import com.hsf302.ch4.dto.EnrollmentView;
import com.hsf302.ch4.dto.StudentCreditDTO;
import com.hsf302.ch4.pojo.Course;
import org.springframework.data.domain.Page;
import com.hsf302.ch4.pojo.Student;

import java.util.List;

public interface EnrollmentService {
    List<Course> getCoursesOfStudent(String studentCode);
    List<Student> getStudentsOfCourse(String courseCode);

    List<Student> findStudentsInCourse(String courseCode);
    long countStudentsInCourse(String courseCode);
    List<Student> findActiveStudentsInCourse(String courseCode);
    List<Student> findStudentsWithoutCourses();
    boolean isEnrolled(String studentCode, String courseCode);
    List<Student> findGoodStudentsInCourse(String courseCode, double minGpa);
    List<StudentCreditDTO> getCreditSummary(int minCredits);
    List<Student> findStudentsWithMoreThan(int n);
    Student getStudentWithCourses(String studentCode);
    List<EnrollmentView> getEnrollmentsOfDepartment(String deptCode);
    Page<Student> findStudentsInCoursePage(String courseCode, int pageIndex, int size);
}