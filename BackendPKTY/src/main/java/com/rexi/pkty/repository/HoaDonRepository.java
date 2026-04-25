package com.rexi.pkty.repository;

import com.rexi.pkty.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    // Gọi Stored Procedure lập hóa đơn mới
    @Query(value = "EXEC sp_LapHoaDon :apptId, :taxRate, :discount, :staffId, :note", nativeQuery = true)
    List<Map<String, Object>> callSpLapHoaDon(
        @Param("apptId") Integer apptId,
        @Param("taxRate") BigDecimal taxRate,
        @Param("discount") BigDecimal discount,
        @Param("staffId") Integer staffId,
        @Param("note") String note
    );

    // Lấy thuốc sắp hết hạn từ View
    @Query(value = "SELECT * FROM v_ThuocSapHetHan", nativeQuery = true)
    List<Map<String, Object>> getThuocSapHetHan();

    // Lấy báo cáo doanh thu tháng từ View
    @Query(value = "SELECT * FROM v_DoanhThu_TheoThang", nativeQuery = true)
    List<Map<String, Object>> getDoanhThuThang();

    // Lấy danh sách hóa đơn theo ID khách hàng
    @Query(value = "SELECT hd.*, t.ten_thu_cung, k.ten_khach_hang FROM HoaDon hd " +
                   "LEFT JOIN LichHen l ON hd.id_lich_hen = l.id_lich_hen " +
                   "LEFT JOIN ThuCung t ON l.id_thu_cung = t.id_thu_cung " +
                   "LEFT JOIN KhachHang k ON hd.id_khach_hang = k.id_khach_hang " +
                   "WHERE hd.id_khach_hang = :customerId " +
                   "ORDER BY hd.id_hoa_don DESC", nativeQuery = true)
    List<Map<String, Object>> findByCustomerId(@Param("customerId") Integer customerId);

    // Lấy tất cả hóa đơn (cho Admin)
    @Query(value = "SELECT hd.*, t.ten_thu_cung, k.ten_khach_hang FROM HoaDon hd " +
                   "LEFT JOIN LichHen l ON hd.id_lich_hen = l.id_lich_hen " +
                   "LEFT JOIN ThuCung t ON l.id_thu_cung = t.id_thu_cung " +
                   "LEFT JOIN KhachHang k ON t.id_khach_hang = k.id_khach_hang " +
                   "ORDER BY hd.ngay_lap_hoa_don DESC", nativeQuery = true)
    List<Map<String, Object>> getAllHoaDon();

    // Lấy danh sách thuốc
    @Query(value = "SELECT * FROM Thuoc", nativeQuery = true)
    List<Map<String, Object>> getAllThuoc();

    // Lấy danh sách lô thuốc
    @Query(value = "SELECT * FROM LoThuoc", nativeQuery = true)
    List<Map<String, Object>> getAllLoThuoc();

    // Báo cáo doanh thu theo tháng (View)
    @Query(value = "SELECT Nam, Thang, TongDoanhThu FROM v_DoanhThu_TheoThang ORDER BY Nam DESC, Thang DESC", nativeQuery = true)
    List<Map<String, Object>> getDoanhThuTheoThang();

    // Thống kê theo bác sĩ (View)
    @Query(value = "SELECT TenBacSi, SoLichHen, SoHoSo, TongDoanhThu FROM v_ThongKe_BacSi", nativeQuery = true)
    List<Map<String, Object>> getThongKeBacSi();
}
