package com.rexi.pkty.controller;

import com.rexi.pkty.repository.HoSoBenhAnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
@CrossOrigin(origins = "*")
public class SystemController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/chuc-nang")
    public List<Map<String, Object>> getChucNang() {
        return jdbcTemplate.queryForList("SELECT * FROM ChucNang");
    }

    @GetMapping("/cau-hinh")
    public List<Map<String, Object>> getCauHinh() {
        // Giả lập từ bảng NhatKy hoặc bảng cấu hình nếu có, ở đây dùng query đơn giản
        return jdbcTemplate.queryForList("SELECT 1 as id_cau_hinh, 'MAX_APPOINTMENTS' as ten_cau_hinh, '50' as gia_tri, 'Số lịch hẹn tối đa' as mo_ta");
    }

    @GetMapping("/debug/columns")
    public List<Map<String, Object>> getColumns() {
        return jdbcTemplate.queryForList("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'LichHen'");
    }
}
