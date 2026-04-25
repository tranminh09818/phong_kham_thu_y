package com.rexi.pkty.controller;

import com.rexi.pkty.entity.LichLamViecNhanVien;
import com.rexi.pkty.entity.NhanVien;
import com.rexi.pkty.repository.LichLamViecNhanVienRepository;
import com.rexi.pkty.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class NhanVienController {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private LichLamViecNhanVienRepository lichLamViecRepository;

    // Lấy danh sách toàn bộ nhân viên
    @GetMapping("/nhan-vien")
    public List<NhanVien> getAllNhanVien() {
        return nhanVienRepository.findAll();
    }

    // Lấy danh sách bác sĩ
    @GetMapping("/bac-si")
    public List<NhanVien> getBacSi() {
        return nhanVienRepository.findAllBacSi();
    }

    // Lấy thống kê bác sĩ từ View
    @GetMapping("/bac-si/thong-ke")
    public List<Map<String, Object>> getBacSiStats() {
        return nhanVienRepository.getBacSiStats();
    }

    // Lấy lịch làm việc nhân viên
    @GetMapping("/nhan-vien/lich-lam-viec")
    public List<LichLamViecNhanVien> getLichLamViec(@RequestParam(required = false) Integer id_nhan_vien) {
        if (id_nhan_vien != null) {
            return lichLamViecRepository.findByIdNhanVien(id_nhan_vien);
        }
        return lichLamViecRepository.findAll();
    }
    // Lấy thông tin chi tiết 1 nhân viên
    @GetMapping("/nhan-vien/profile/{id}")
    public NhanVien getProfile(@PathVariable Integer id) {
        return nhanVienRepository.findById(id).orElse(null);
    }

    // Cập nhật thông tin nhân viên
    @PutMapping("/nhan-vien/{id}")
    public NhanVien updateNhanVien(@PathVariable Integer id, @RequestBody NhanVien nv) {
        nv.setId_nhan_vien(id);
        return nhanVienRepository.save(nv);
    }
}
