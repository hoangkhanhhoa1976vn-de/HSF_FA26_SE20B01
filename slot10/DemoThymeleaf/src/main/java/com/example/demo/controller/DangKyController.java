package com.example.demo.controller;

import com.example.demo.model.NguoiDung;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dangky")
public class DangKyController {

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());
        return "dangky/form";
    }

    @PostMapping
    public String xuLy(@Valid @ModelAttribute("nguoiDung") NguoiDung nguoiDung,
                       BindingResult result,
                       RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "dangky/form";
        }
        ra.addFlashAttribute("hoTen", nguoiDung.getHoTen());
        return "redirect:/dangky/thanh-cong";
    }

    @GetMapping("/thanh-cong")
    public String thanhCong() {
        return "dangky/thanh-cong";
    }
}
