package com.rexi.pkty.controller;

import com.rexi.pkty.entity.HoSoBenhAn;
import com.rexi.pkty.entity.LichHen;
import com.rexi.pkty.repository.HoSoBenhAnRepository;
import com.rexi.pkty.repository.LichHenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * FIX #8: Error Handling & #10: Transaction Management
 * - No detailed error messages exposed to client
 * - Uses logging instead of printStackTrace
 * - Transactional operations for data consistency
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${cors.allowed-origins:http://localhost:3000}")
public class LichHenController {

    private static final Logger logger = Logger.getLogger(LichHenController.class.getName());

    @Autowired
    private LichHenRepository lichHenRepository;

    @Autowired
    private HoSoBenhAnRepository hoSoBenhAnRepository;

    // Lấy danh sách lịch hẹn hôm nay (từ View)
    @GetMapping("/lich-hen/hom-nay")
    public List<Map<String, Object>> getTodayAppointments() {
        try {
            return lichHenRepository.getTodayAppointments();
        } catch (Exception e) {
            logger.severe("Error fetching today appointments: " + e.getMessage());
            return List.of();
        }
    }

    // Lấy lịch hẹn theo khách hàng
    @GetMapping("/lich-hen/khach/{id}")
    public List<LichHen> getByCustomer(@PathVariable Integer id) {
        try {
            return lichHenRepository.findByIdKhachHang(id);
        } catch (Exception e) {
            logger.severe("Error fetching appointments for customer: " + e.getMessage());
            return List.of();
        }
    }

    // Đếm tổng số lịch hẹn (cho Dashboard)
    @GetMapping("/lich-hen/count")
    public Long countAll() {
        try {
            return lichHenRepository.count();
        } catch (Exception e) {
            logger.severe("Error counting appointments: " + e.getMessage());
            return 0L;
        }
    }

    /**
     * FIX #8: Đặt lịch hẹn mới - Improved Error Handling
     * FIX #10: Transactional
     */
    @PostMapping("/lich-hen")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> addAppointment(@RequestBody LichHen lh) {
        try {
            // Validate input
            if (lh.getId_khach_hang() == null) {
                logger.warning("Missing customer ID in appointment booking");
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Thiếu thông tin khách hàng"));
            }

            // Thiết lập các giá trị mặc định
            if (lh.getTrang_thai() == null) lh.setTrang_thai("da_dat");
            if (lh.getPhong_kham() == null) lh.setPhong_kham("Phòng 01");
            
            LichHen saved = lichHenRepository.save(lh);
            logger.info("Appointment booked successfully. ID: " + saved.getId_lich_hen());
            
            return ResponseEntity.ok(Map.of(
                "ThongBao", "Đặt lịch thành công! Mã số của bạn là #" + saved.getId_lich_hen(),
                "id_lich_hen", saved.getId_lich_hen()
            ));
        } catch (Exception e) {
            logger.severe("Error booking appointment: " + e.getMessage());
            // FIX #8: Do NOT expose detailed error messages
            return ResponseEntity.status(400).body(Map.of(
                "message", "Lỗi đặt lịch. Vui lòng kiểm tra lại dữ liệu."
            ));
        }
    }

    /**
     * FIX #8 & #10: Tạo bệnh án mới - Better error handling + transaction
     */
    @PostMapping("/benh-an")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> addMedicalRecord(@RequestBody HoSoBenhAn hs) {
        try {
            if (hs.getId_lich_hen() == null) {
                logger.warning("Missing appointment ID in medical record");
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Thiếu thông tin lịch hẹn"));
            }

            List<Map<String, Object>> result = hoSoBenhAnRepository.callSpAddMedicalRecord(
                hs.getId_lich_hen(),
                hs.getNgay_kham(),
                hs.getId_bac_si(),
                hs.getCan_nang(),
                hs.getNhiet_do(),
                hs.getTrieu_chung(),
                hs.getChan_doan(),
                hs.getPhac_do_dieu_tri(),
                hs.getHuong_dan_cham_soc(),
                hs.getId_nguoi_tao()
            );
            
            logger.info("Medical record created successfully for appointment: " + hs.getId_lich_hen());
            return ResponseEntity.ok(result.get(0));
        } catch (Exception e) {
            logger.severe("Error creating medical record: " + e.getMessage());
            return ResponseEntity.status(400).body(Map.of(
                "message", "Lỗi tạo bệnh án. Vui lòng thử lại sau."
            ));
        }
    }

    // Lấy kết quả xét nghiệm theo bệnh án
    @GetMapping("/xet-nghiem/{id_ho_so}")
    public List<Map<String, Object>> getXetNghiem(@PathVariable Integer id_ho_so) {
        try {
            return hoSoBenhAnRepository.findXetNghiemByHoSo(id_ho_so);
        } catch (Exception e) {
            logger.severe("Error fetching test results: " + e.getMessage());
            return List.of();
        }
    }

    // Lấy tất cả lịch hẹn (cho Admin)
    @GetMapping("/lich-hen")
    public List<Map<String, Object>> getAllAppointments() {
        try {
            return lichHenRepository.getAllAppointments();
        } catch (Exception e) {
            logger.severe("Error fetching all appointments: " + e.getMessage());
            return List.of();
        }
    }
}
