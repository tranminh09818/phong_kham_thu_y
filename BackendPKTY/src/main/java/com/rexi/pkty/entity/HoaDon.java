package com.rexi.pkty.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDon") // Kết nối bảng hóa đơn
@Data
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_hoa_don;

    private Integer id_lich_hen;
    private Integer id_khach_hang;
    private BigDecimal tong_tien_truoc_giam_gia;
    private BigDecimal tong_tien_giam_gia;
    private BigDecimal tong_tien_sau_giam_gia;
    private BigDecimal thue_suat;
    private BigDecimal thue_phai_nop;
    private BigDecimal tong_tien_cuoi;
    private LocalDateTime ngay_lap;
    private Integer id_nhan_vien;
    private String trang_thai;
    private String ghi_chu;
}
