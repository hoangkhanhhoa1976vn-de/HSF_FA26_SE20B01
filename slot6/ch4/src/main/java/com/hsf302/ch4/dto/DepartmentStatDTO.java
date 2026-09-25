package com.hsf302.ch4.dto;

public record DepartmentStatDTO(String code, String name, Long studentCount, Double avgGpa) {

    @Override
    public String toString() {
        return String.format("%s - %-25s | students: %d | avg GPA: %s",
                code, name, studentCount,
                avgGpa == null ? "null" : String.format("%.3f", avgGpa));
    }
}
