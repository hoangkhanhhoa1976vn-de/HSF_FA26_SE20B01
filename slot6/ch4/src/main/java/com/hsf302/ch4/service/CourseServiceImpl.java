package com.hsf302.ch4.service;

import com.hsf302.ch4.dto.CourseEnrollmentCount;
import com.hsf302.ch4.dto.CourseStatDTO;
import com.hsf302.ch4.dto.DepartmentCreditStat;
import com.hsf302.ch4.dto.SemesterStatDTO;
import com.hsf302.ch4.pojo.Course;
import com.hsf302.ch4.repository.CourseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.hsf302.ch4.pojo.Student;

@Service
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public long count() {
        return courseRepository.count();
    }

    @Override
    public List<Course> findAllOrderByCode() {
        return courseRepository.findAll(Sort.by("code"));
    }

    @Override
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public Optional<Course> findByCode(String code) {
        return courseRepository.findByCode(code);
    }

    @Override
    public List<Course> findBySemester(String semester) {
        return courseRepository.findBySemesterOrderByCodeAsc(semester);
    }

    @Override
    public long countBySemester(String semester) {
        return courseRepository.countBySemester(semester);
    }

    @Override
    public List<Course> findCoursesOfStudent(String studentCode) {
        return courseRepository.findByStudents_StudentCodeOrderByCodeAsc(studentCode);
    }

    @Override
    public List<Course> findCoursesOfDepartment(String deptCode, boolean distinct) {
        return distinct
                ? courseRepository.findDistinctByStudents_Department_CodeOrderByCodeAsc(deptCode)
                : courseRepository.findByStudents_Department_CodeOrderByCodeAsc(deptCode);
    }

    @Override
    public List<Course> findCoursesWithoutStudents() {
        return courseRepository.findByStudentsIsEmpty();
    }

    @Override
    public List<CourseStatDTO> getStatistics() {
        return courseRepository.getCourseStats();
    }

    @Override
    public List<Course> findFullCourses() {
        return courseRepository.findFullCourses();
    }

    @Override
    public Course getWithStudents(String code) {
        return courseRepository.findWithStudentsByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + code));
    }

    @Override
    public List<CourseEnrollmentCount> findTopEnrolled(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be > 0");
        }
        return courseRepository.findTopEnrolledNative(n);
    }

    @Override
    @Transactional
    public void deleteCourseDirectly(String code) {
        Course c = getCourse(code);
        courseRepository.delete(c);
        courseRepository.flush();          // ép Hibernate chạy DELETE ngay để thấy lỗi
    }

    @Override
    @Transactional
    public int deleteCourse(String code) {
        Course c = getCourse(code);
        // copy ra Set mới: unenroll() sẽ sửa c.getStudents() -> tránh ConcurrentModificationException
        Set<Student> students = new HashSet<>(c.getStudents());
        students.forEach(s -> s.unenroll(c));   // gỡ từ OWNING side -> DELETE các dòng student_courses
        courseRepository.delete(c);             // sau đó mới DELETE courses
        return students.size();
    }

    private Course getCourse(String code) {
        return courseRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + code));
    }


    // ===== Triển khai câu hỏi kiểm tra cô Bích Tra =====
    @Override
    public List<Course> findByCreditsRange(int min, int max, boolean useCustomQuery) {
        if (min > max) {
            throw new IllegalArgumentException("min credits must be <= max credits");
        }
        return useCustomQuery
                ? courseRepository.findCoursesByCreditsRange(min, max)
                : courseRepository.findByCreditsBetween(min, max);
    }

    @Override
    public long countCoursesWithCreditsGreaterThan(int credits, boolean useCustomQuery) {
        return useCustomQuery
                ? courseRepository.countCoursesWithCreditsGreaterThan(credits)
                : courseRepository.countByCreditsGreaterThan(credits);
    }

    @Override
    public List<Course> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return courseRepository.searchByNameCustom(keyword.trim());
    }

    @Override
    public List<SemesterStatDTO> getSemesterStats() {
        return courseRepository.getSemesterStats();
    }

    @Override
    public List<DepartmentCreditStat> getDepartmentCreditsNative() {
        return courseRepository.getDepartmentCreditsNative();
    }
}