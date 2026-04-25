package com.rexi.pkty.controller;

import com.rexi.pkty.entity.HoaDon;
import com.rexi.pkty.repository.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FinanceController {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    // Lập hóa đơn mới (Dùng SP)
    @PostMapping("/hoa-don")
    public ResponseEntity<?> addInvoice(@RequestBody HoaDon hd) {
        try {
            List<Map<String, Object>> result = hoaDonRepository.callSpLapHoaDon(
                hd.getId_lich_hen(),
                hd.getThue_suat(),
                hd.getTong_tien_giam_gia(),
                hd.getId_nhan_vien(),
                hd.getGhi_chu()
            );
            return ResponseEntity.ok(result.get(0));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", "Lỗi lập hóa đơn: " + e.getMessage()));
        }
    }

    // Lấy danh sách thuốc sắp hết hạn (từ View)
    @GetMapping("/kho/thuoc-sap-het-han")
    public List<Map<String, Object>> getThuocSapHetHan() {
        return hoaDonRepository.getThuocSapHetHan();
    }

    // Lấy danh sách hóa đơn theo ID khách hàng
    @GetMapping("/hoa-don/khach/{id}")
    public ResponseEntity<?> getInvoicesByCustomerId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(hoaDonRepository.findByCustomerId(id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi truy xuất hóa đơn: " + e.getMessage());
        }
    }

    // Lấy tất cả hóa đơn (cho Admin)
    @GetMapping("/hoa-don")
    public ResponseEntity<?> getAllInvoices() {
        try {
            return ResponseEntity.ok(hoaDonRepository.getAllHoaDon());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi truy xuất danh sách hóa đơn: " + e.getMessage());
        }
    }

    // Lấy tất cả thuốc
    @GetMapping("/kho/thuoc")
    public ResponseEntity<?> getAllThuoc() {
        try {
            return ResponseEntity.ok(hoaDonRepository.getAllThuoc());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi truy xuất danh sách thuốc: " + e.getMessage());
        }
    }

    // Lấy tất cả lô thuốc
    @GetMapping("/kho/lo-thuoc")
    public ResponseEntity<?> getAllLoThuoc() {
        try {
            return ResponseEntity.ok(hoaDonRepository.getAllLoThuoc());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi truy xuất danh sách lô thuốc: " + e.getMessage());
        }
    }

    // Báo cáo doanh thu tháng (từ View)
    @GetMapping("/bao-cao/doanh-thu-thang")
    public ResponseEntity<?> getDoanhThuThang() {
        try {
            return ResponseEntity.ok(hoaDonRepository.getDoanhThuTheoThang());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi truy xuất báo cáo doanh thu: " + e.getMessage());
        }
    }

    // Thống kê bác sĩ (từ View)
    @GetMapping("/bao-cao/thong-ke-bac-si")
    public ResponseEntity<?> getThongKeBacSi() {
        try {
            return ResponseEntity.ok(hoaDonRepository.getThongKeBacSi());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi truy xuất thống kê bác sĩ: " + e.getMessage());
        }
    }
}
