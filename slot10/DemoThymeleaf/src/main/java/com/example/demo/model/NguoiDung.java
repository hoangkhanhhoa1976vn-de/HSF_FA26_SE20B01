package com.example.demo.model;

import jakarta.validation.constraints.*;

public class NguoiDung {

    @NotBlank(message = "{validation.ten.notblank}")
    @Size(min = 2, max = 50, message = "{validation.ten.size}")
    private String hoTen;

    @NotBlank(message = "{validation.email.notblank}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotNull(message = "{validation.tuoi.notnull}")
    @Min(value = 18, message = "{validation.tuoi.min}")
    @Max(value = 100, message = "{validation.tuoi.max}")
    private Integer tuoi;

    // Không bắt buộc: chấp nhận rỗng HOẶC đúng 10 chữ số
    @Pattern(regexp = "^$|^[0-9]{10}$", message = "{validation.sdt.pattern}")
    private String soDienThoai;

    public NguoiDung() {}

    public NguoiDung(String hoTen, String email, Integer tuoi, String soDienThoai) {
        this.hoTen = hoTen;
        this.email = email;
        this.tuoi = tuoi;
        this.soDienThoai = soDienThoai;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTuoi() {
        return tuoi;
    }

    public void setTuoi(Integer tuoi) {
        this.tuoi = tuoi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}
