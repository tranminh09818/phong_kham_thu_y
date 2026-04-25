package com.rexi.pkty.controller;

import com.rexi.pkty.entity.KhachHang;
import com.rexi.pkty.repository.KhachHangRepository;
import com.rexi.pkty.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/khach-hang")
@CrossOrigin(origins = "*")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private KhachHangRepository khachHangRepository;

    // Lấy danh sách khách hàng
    @GetMapping
    public List<KhachHang> getAll() {
        return khachHangService.getAllKhachHang();
    }

    // Đếm tổng số khách hàng (cho Dashboard)
    @GetMapping("/count")
    public Long countAll() {
        return khachHangRepository.count();
    }

    // Lấy thông tin 1 khách hàng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return khachHangRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Cập nhật thông tin khách hàng (Dùng Stored Procedure)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody KhachHang kh) {
        try {
            List<Map<String, Object>> result = khachHangRepository.callSpUpdateKhachHang(
                id,
                kh.getTen_khach_hang(),
                kh.getEmail(),
                kh.getSdt(),
                kh.getDia_chi()
            );
            
            if (result != null && !result.isEmpty()) {
                return ResponseEntity.ok(result.get(0));
            }
            return ResponseEntity.ok(Map.of("message", "Cập nhật thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", "Lỗi cập nhật: " + e.getMessage()));
        }
    }

    // Vô hiệu hóa tài khoản (Vùng nguy hiểm)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivate(@PathVariable Integer id) {
        try {
            khachHangRepository.deactivateAccountByKhachHangId(id);
            return ResponseEntity.ok(Map.of("message", "Vô hiệu hóa tài khoản thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }
}
