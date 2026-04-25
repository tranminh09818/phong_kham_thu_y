package com.rexi.pkty.controller;

import com.rexi.pkty.entity.ThuCung;
import com.rexi.pkty.entity.TiemChung;
import com.rexi.pkty.repository.ThuCungRepository;
import com.rexi.pkty.repository.TiemChungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thu-cung")
@CrossOrigin(origins = "*")
public class ThuCungController {

    @Autowired
    private ThuCungRepository thuCungRepository;

    @Autowired
    private TiemChungRepository tiemChungRepository;

    // Lấy danh sách thú cưng toàn hệ thống
    @GetMapping
    public List<ThuCung> getAll() {
        return thuCungRepository.findAll();
    }

    // Lấy danh sách Pet của một chủ
    @GetMapping("/khach/{id}")
    public List<ThuCung> getByOwner(@PathVariable Integer id) {
        return thuCungRepository.findByKhachHang(id);
    }

    // Lấy lịch sử tiêm phòng của 1 thú cưng
    @GetMapping("/{id}/tiem-chung")
    public List<TiemChung> getVaccinationHistory(@PathVariable Integer id) {
        return tiemChungRepository.findByIdThuCung(id);
    }

    // Thêm thú cưng mới
    @PostMapping
    public ResponseEntity<?> createPet(@RequestBody ThuCung thuCung) {
        try {
            thuCung.setNgay_tao(java.time.LocalDateTime.now());
            thuCung.setDa_xoa(false);
            ThuCung saved = thuCungRepository.save(thuCung);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of("message", "Lỗi Backend: " + e.getMessage()));
        }
    }

    // Cập nhật thông tin thú cưng
    @PutMapping("/{id}")
    public ThuCung updatePet(@PathVariable Integer id, @RequestBody ThuCung petDetails) {
        ThuCung pet = thuCungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));

        pet.setTen_thu_cung(petDetails.getTen_thu_cung());
        pet.setLoai(petDetails.getLoai());
        pet.setGiong(petDetails.getGiong());
        pet.setNgay_sinh(petDetails.getNgay_sinh());
        pet.setGioi_tinh(petDetails.getGioi_tinh());
        pet.setMau_sac(petDetails.getMau_sac());
        pet.setTrong_luong(petDetails.getTrong_luong());
        // Giữ nguyên id_khach_hang, ngay_tao, da_xoa

        return thuCungRepository.save(pet);
    }

    // Xóa thú cưng (Soft delete hoặc Hard delete)
    @DeleteMapping("/{id}")
    public void deletePet(@PathVariable Integer id) {
        // Có thể dùng soft delete
        ThuCung pet = thuCungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));
        pet.setDa_xoa(true);
        thuCungRepository.save(pet);
        // Hoặc hard delete: thuCungRepository.deleteById(id);
    }
}
