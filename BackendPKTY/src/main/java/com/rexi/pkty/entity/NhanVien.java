package com.rexi.pkty.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "NhanVien") // Kết nối bảng NhanVien
@Data
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_nhan_vien;

    private Integer id_tai_khoan;
    private String ho_ten;
    private LocalDate ngay_sinh;
    private String gioi_tinh;
    private String dia_chi;
    private String so_dien_thoai;
    private String email;
    private String so_cccd;
    private LocalDate ngay_vao_lam;
    private LocalDate ngay_nghi_viec;
    private String trang_thai;
    private Boolean da_xoa;
}
