package com.rexi.pkty.repository;

import com.rexi.pkty.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    // Lấy danh sách bác sĩ (vai_tro = 'bác sĩ' thông qua join bảng VaiTroHeThong)
    @Query(value = "SELECT nv.* FROM NhanVien nv " +
                   "JOIN TaiKhoan tk ON nv.id_tai_khoan = tk.id_tai_khoan " +
                   "JOIN VaiTroHeThong vt ON tk.id_vai_tro = vt.id_vai_tro " +
                   "WHERE vt.ten_vai_tro = N'bác sĩ' AND nv.da_xoa = 0", nativeQuery = true)
    List<NhanVien> findAllBacSi();

    // Lấy dữ liệu từ View v_ThongKe_BacSi
    @Query(value = "SELECT * FROM v_ThongKe_BacSi", nativeQuery = true)
    List<Map<String, Object>> getBacSiStats();
}
