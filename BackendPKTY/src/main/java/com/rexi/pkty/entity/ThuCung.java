package com.rexi.pkty.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThuCung") // Kết nối bảng thú cưng
@Data
public class ThuCung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_thu_cung;

    private String ten_thu_cung;
    private String loai;
    private String giong;
    private LocalDate ngay_sinh;
    private String gioi_tinh;
    private String mau_sac;
    private BigDecimal trong_luong;
    private Integer id_khach_hang;
    private LocalDateTime ngay_tao;
    private Boolean da_xoa;
}
