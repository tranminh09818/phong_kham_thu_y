package com.rexi.pkty.repository;

import com.rexi.pkty.entity.ThuCung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ThuCungRepository extends JpaRepository<ThuCung, Integer> {

    // Tìm pet theo chủ (id_khach_hang)
    @Query("SELECT t FROM ThuCung t WHERE t.id_khach_hang = :idKhachHang AND t.da_xoa = false")
    List<ThuCung> findByKhachHang(@Param("idKhachHang") Integer idKhachHang);
}
