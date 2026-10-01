package com.hsf302.ch4.dto;

public record CourseStatDTO(String code, String name, Integer capacity,
                            Long enrolled, Double avgGpa) {

    public long remaining() {
        return capacity - enrolled;
    }
}