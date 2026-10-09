package com.hsf302.chapter6.controller;

import com.hsf302.chapter6.entity.Student;
import com.hsf302.chapter6.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    private static final String FORM_VIEW = "students/form";

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @ModelAttribute("majors")
    public List<String> majors() {
        return studentService.getMajors();
    }

    // ==================== READ ALL ====================

    @GetMapping
    public String list(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "students/list";
    }

    // ==================== READ ONE ====================

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        return studentService.findById(id)
                .map(student -> {
                    model.addAttribute("student", student);
                    return "students/detail";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMsg", "Không tìm thấy sinh viên ID: " + id);
                    return "redirect:/students";
                });
    }

    // ==================== CREATE ====================

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        return formView(model, false);
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("student") Student student,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        if (!bindingResult.hasFieldErrors("email")
                && studentService.isEmailTaken(student.getEmail(), null)) {
            bindingResult.rejectValue("email", "duplicate", "Email đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return formView(model, false);
        }
        try {
            studentService.create(student);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("email", "duplicate", "Email đã tồn tại");
            return formView(model, false);
        }
        ra.addFlashAttribute("successMsg", "Thêm sinh viên thành công!");
        return "redirect:/students";
    }

    // ==================== UPDATE ====================

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        return studentService.findById(id)
                .map(student -> {
                    model.addAttribute("student", student);
                    return formView(model, true);
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMsg", "Không tìm thấy sinh viên ID: " + id);
                    return "redirect:/students";
                });
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("student") Student student,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        student.setId(id);

        if (!bindingResult.hasFieldErrors("email")
                && studentService.isEmailTaken(student.getEmail(), id)) {
            bindingResult.rejectValue("email", "duplicate", "Email đã được sinh viên khác sử dụng");
        }
        if (bindingResult.hasErrors()) {
            return formView(model, true);
        }
        try {
            if (studentService.update(id, student)) {
                ra.addFlashAttribute("successMsg", "Cập nhật thành công!");
            } else {
                ra.addFlashAttribute("errorMsg", "Không tìm thấy sinh viên ID: " + id);
            }
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("email", "duplicate", "Email đã được sinh viên khác sử dụng");
            return formView(model, true);
        }
        return "redirect:/students";
    }

    // ==================== DELETE ====================

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
        if (studentService.delete(id)) {
            ra.addFlashAttribute("successMsg", "Xóa sinh viên thành công!");
        } else {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy sinh viên để xóa!");
        }
        return "redirect:/students";
    }

    // ==================== Helper ====================

    private String formView(Model model, boolean isEdit) {
        model.addAttribute("isEdit", isEdit);
        model.addAttribute("pageTitle", isEdit ? "Cập nhật sinh viên" : "Thêm sinh viên mới");
        return FORM_VIEW;
    }
}
