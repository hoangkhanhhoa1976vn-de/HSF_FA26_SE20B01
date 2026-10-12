package com.example.demo.controller;

import com.example.demo.model.SanPham;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@RequestMapping("/sanpham")
public class SanPhamController {

    private final List<SanPham> danhSach = new CopyOnWriteArrayList<>();

    @GetMapping("/them")
    public String showForm(Model model) {
        model.addAttribute("sanPham", new SanPham());
        return "sanpham/form";
    }

    @PostMapping("/them")
    public String xuLyForm(@ModelAttribute("sanPham") SanPham sanPham, RedirectAttributes ra) {
        danhSach.add(sanPham);
        ra.addFlashAttribute("thongBao", "Thêm sản phẩm thành công!");
        ra.addFlashAttribute("successMsg", "Thêm sản phẩm thành công!");
        return "redirect:/sanpham/ket-qua";
    }

    @GetMapping("/ket-qua")
    public String ketQua(Model model) {
        model.addAttribute("danhSach", danhSach);
        return "sanpham/ket-qua";
    }
}
