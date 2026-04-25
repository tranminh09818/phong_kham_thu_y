package com.rexi.pkty.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "KhachHang") // Kết nối bảng KhachHang
@Data
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_khach_hang;

    private String ten_khach_hang;
    private String email;
    private String sdt;
    private String dia_chi;
    private LocalDateTime ngay_tao;
    private LocalDateTime ngay_cap_nhat;
    private Boolean da_xoa;
}
