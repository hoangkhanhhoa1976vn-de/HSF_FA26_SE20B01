package com.hsf302.ch4.dto;

public interface StudentSummary {
    String getStudentCode();
    String getFullName();
    Double getGpa();
    String getDepartmentName();

    default String toDisplayString() {
        return String.format("%s | %-15s | %.1f | %s",
                getStudentCode(), getFullName(), getGpa(), getDepartmentName());
    }
}
