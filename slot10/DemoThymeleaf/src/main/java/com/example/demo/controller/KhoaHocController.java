package com.example.demo.controller;

import com.example.demo.model.KhoaHoc;
import com.example.demo.service.KhoaHocService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/khoahoc")
public class KhoaHocController {

    private final KhoaHocService service;

    public KhoaHocController(KhoaHocService service) {
        this.service = service;
    }

    @GetMapping
    public String danhSach(@RequestParam(value = "keyword", required = false) String keyword,
                           Model model) {
        model.addAttribute("danhSach", service.search(keyword));
        model.addAttribute("keyword", keyword == null ? "" : keyword.trim());
        return "khoahoc/danh-sach";
    }

    @GetMapping("/them")
    public String themMoi(Model model) {
        model.addAttribute("khoaHoc", new KhoaHoc());
        model.addAttribute("pageTitle", "Thêm khóa học");
        return "khoahoc/form";
    }

    @GetMapping("/sua/{id}")
    public String sua(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        return service.findById(id)
                .map(kh -> {
                    model.addAttribute("khoaHoc", kh);
                    model.addAttribute("pageTitle", "Sửa khóa học");
                    return "khoahoc/form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMsg", "Không tìm thấy khóa học ID: " + id);
                    return "redirect:/khoahoc";
                });
    }

    @PostMapping("/luu")
    public String luu(@ModelAttribute("khoaHoc") KhoaHoc khoaHoc, RedirectAttributes ra) {
        boolean isNew = (khoaHoc.getId() == null);
        service.save(khoaHoc);
        ra.addFlashAttribute("successMsg", isNew ? "Thêm khóa học thành công!" : "Cập nhật thành công!");
        return "redirect:/khoahoc";
    }

    @PostMapping("/xoa/{id}")
    public String xoa(@PathVariable("id") Long id, RedirectAttributes ra) {
        if (service.delete(id)) {
            ra.addFlashAttribute("successMsg", "Đã xóa khóa học!");
        } else {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy khóa học để xóa!");
        }
        return "redirect:/khoahoc";
    }
}
