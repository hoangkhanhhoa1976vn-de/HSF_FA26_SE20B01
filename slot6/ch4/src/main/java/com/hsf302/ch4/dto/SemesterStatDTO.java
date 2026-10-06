package com.hsf302.ch4.dto;

public record SemesterStatDTO(
    String semester,
    Long courseCount,
    Long totalEnrollments
) {}
