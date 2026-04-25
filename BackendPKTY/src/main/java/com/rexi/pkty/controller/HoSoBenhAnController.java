package com.rexi.pkty.controller;

import com.rexi.pkty.repository.HoSoBenhAnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ho-so-benh-an")
@CrossOrigin(origins = "*")
public class HoSoBenhAnController {

    @Autowired
    private HoSoBenhAnRepository hoSoBenhAnRepository;

    @GetMapping("/khach/{id}")
    public ResponseEntity<?> getByCustomerId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(hoSoBenhAnRepository.findByCustomerId(id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi truy xuất hồ sơ bệnh án: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(hoSoBenhAnRepository.getAllHoSoBenhAn());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi truy xuất danh sách hồ sơ: " + e.getMessage());
        }
    }

    // Lấy tất cả xét nghiệm (cho Admin)
    @GetMapping("/xet-nghiem")
    public ResponseEntity<?> getAllXetNghiem() {
        try {
            return ResponseEntity.ok(hoSoBenhAnRepository.getAllXetNghiem());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi truy xuất danh sách xét nghiệm: " + e.getMessage());
        }
    }

    // Lấy tất cả đơn thuốc (cho Admin)
    @GetMapping("/don-thuoc")
    public ResponseEntity<?> getAllDonThuoc() {
        try {
            return ResponseEntity.ok(hoSoBenhAnRepository.getAllDonThuoc());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi truy xuất danh sách đơn thuốc: " + e.getMessage());
        }
    }
}
