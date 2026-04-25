package com.rexi.pkty.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "LichHen") // Kết nối bảng lịch hẹn
@Data
public class LichHen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_lich_hen;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngay_kham;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "HH:mm:ss")
    private LocalTime gio_kham;
    private String ly_do;
    private String trang_thai;
    private Integer id_khach_hang;
    private Integer id_thu_cung;
    private Integer id_bac_si;
    private Integer id_nguoi_dat;
    private String phong_kham;
    private String ghi_chu_noi_bo;
    private LocalDateTime ngay_tao;

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        if (this.ngay_tao == null) {
            this.ngay_tao = java.time.LocalDateTime.now();
        }
    }
}
