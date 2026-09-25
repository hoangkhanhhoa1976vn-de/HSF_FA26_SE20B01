package com.hsf302.ch4.service;

import com.hsf302.ch4.dto.DepartmentStatDTO;
import com.hsf302.ch4.pojo.Department;
import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    long count();
    boolean existsById(Long id);
    List<Department> findDepartmentsWithoutStudents();
    List<DepartmentStatDTO> getStatistics();
    Optional<Department> findByCode(String code);
    Department getWithStudents(String code);
    int transferStudentsAndDelete(String fromCode, String toCode);
    List<Department> findAll();
}
