package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "khoa_hoc")
public class KhoaHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_khoa_hoc", nullable = false, length = 100)
    private String tenKhoaHoc;

    @Column(name = "giang_vien", length = 100)
    private String giangVien;

    @Column(name = "so_tin_chi")
    private Integer soTinChi;

    public KhoaHoc() {}

    public KhoaHoc(String tenKhoaHoc, String giangVien, Integer soTinChi) {
        this.tenKhoaHoc = tenKhoaHoc;
        this.giangVien = giangVien;
        this.soTinChi = soTinChi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenKhoaHoc() {
        return tenKhoaHoc;
    }

    public void setTenKhoaHoc(String tenKhoaHoc) {
        this.tenKhoaHoc = tenKhoaHoc;
    }

    public String getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(String giangVien) {
        this.giangVien = giangVien;
    }

    public Integer getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(Integer soTinChi) {
        this.soTinChi = soTinChi;
    }
}
