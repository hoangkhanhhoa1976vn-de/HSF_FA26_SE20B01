package com.example.demo;

import com.example.demo.model.KhoaHoc;
import com.example.demo.service.KhoaHocService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ControllerIntegrationTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private KhoaHocService khoaHocService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testTrangChu() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("trang-chu"))
                .andExpect(content().string(containsString("Chào mừng")));
    }

    @Test
    void testSinhVienDanhSach() throws Exception {
        mockMvc.perform(get("/sinhvien"))
                .andExpect(status().isOk())
                .andExpect(view().name("sinhvien/danh-sach"))
                .andExpect(model().attributeExists("sinhViens", "tieuDe"))
                .andExpect(content().string(containsString("SV001")))
                .andExpect(content().string(containsString("Nguyễn Văn An")));
    }

    @Test
    void testSanPhamFlow() throws Exception {
        mockMvc.perform(get("/sanpham/them"))
                .andExpect(status().isOk())
                .andExpect(view().name("sanpham/form"))
                .andExpect(model().attributeExists("sanPham"));

        mockMvc.perform(post("/sanpham/them")
                        .param("ten", "Bút bi")
                        .param("gia", "5000")
                        .param("soLuong", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sanpham/ket-qua"))
                .andExpect(flash().attributeExists("thongBao", "successMsg"));

        mockMvc.perform(get("/sanpham/ket-qua"))
                .andExpect(status().isOk())
                .andExpect(view().name("sanpham/ket-qua"))
                .andExpect(model().attributeExists("danhSach"))
                .andExpect(content().string(containsString("Bút bi")));
    }

    @Test
    void testKhoaHocCrud() throws Exception {
        // List
        mockMvc.perform(get("/khoahoc"))
                .andExpect(status().isOk())
                .andExpect(view().name("khoahoc/danh-sach"))
                .andExpect(model().attributeExists("danhSach"))
                .andExpect(content().string(containsString("Quản lý khóa học")));

        // Search
        mockMvc.perform(get("/khoahoc").param("keyword", "Java"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Lập trình Java")));

        // Add form
        mockMvc.perform(get("/khoahoc/them"))
                .andExpect(status().isOk())
                .andExpect(view().name("khoahoc/form"))
                .andExpect(model().attributeExists("khoaHoc", "pageTitle"));

        // Save new
        mockMvc.perform(post("/khoahoc/luu")
                        .param("tenKhoaHoc", "Thymeleaf nâng cao")
                        .param("giangVien", "Phạm D")
                        .param("soTinChi", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khoahoc"))
                .andExpect(flash().attribute("successMsg", "Thêm khóa học thành công!"));

        // Edit existing
        KhoaHoc first = khoaHocService.search("Thymeleaf nâng cao").get(0);
        mockMvc.perform(get("/khoahoc/sua/" + first.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("khoahoc/form"))
                .andExpect(model().attributeExists("khoaHoc"));

        // Edit non-existent
        mockMvc.perform(get("/khoahoc/sua/999999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khoahoc"))
                .andExpect(flash().attributeExists("errorMsg"));

        // Delete
        mockMvc.perform(post("/khoahoc/xoa/" + first.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khoahoc"))
                .andExpect(flash().attribute("successMsg", "Đã xóa khóa học!"));
    }

    @Test
    void testDangKyValidationAndI18n() throws Exception {
        // Vietnamese default form
        mockMvc.perform(get("/dangky"))
                .andExpect(status().isOk())
                .andExpect(view().name("dangky/form"))
                .andExpect(content().string(containsString("Đăng ký tài khoản")));

        // Invalid submit in Vietnamese
        mockMvc.perform(post("/dangky")
                        .param("hoTen", "")
                        .param("email", "")
                        .param("tuoi", "")
                        .param("soDienThoai", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("dangky/form"))
                .andExpect(model().hasErrors())
                .andExpect(content().string(containsString("Họ tên không được để trống")));

        // English query param
        mockMvc.perform(get("/dangky").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Register Account")));

        // Valid submit
        mockMvc.perform(post("/dangky")
                        .param("hoTen", "Nguyen Van An")
                        .param("email", "an@example.com")
                        .param("tuoi", "20")
                        .param("soDienThoai", "0987654321"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dangky/thanh-cong"))
                .andExpect(flash().attribute("hoTen", "Nguyen Van An"));

        mockMvc.perform(get("/dangky/thanh-cong"))
                .andExpect(status().isOk())
                .andExpect(view().name("dangky/thanh-cong"));
    }
}
