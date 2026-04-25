package com.rexi.pkty.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "TaiKhoan") // Kết nối bảng TaiKhoan
@Data
public class TaiKhoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_tai_khoan;

    private String ten_dang_nhap;
    private String mat_khau;
    private Integer id_vai_tro;
    private String trang_thai;
    private LocalDateTime ngay_tao;
    private Integer id_khach_hang;
    private String mat_khau_hash;
}
