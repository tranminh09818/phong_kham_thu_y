package com.rexi.pkty.controller;

import com.rexi.pkty.entity.DichVu;
import com.rexi.pkty.repository.DichVuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dich-vu")
@CrossOrigin(origins = "*")
public class DichVuController {

    @Autowired
    private DichVuRepository dichVuRepository;

    @GetMapping
    public List<DichVu> getAll() {
        return dichVuRepository.findAll();
    }

    @PostMapping
    public DichVu create(@RequestBody DichVu dv) {
        return dichVuRepository.save(dv);
    }

    @PutMapping("/{id}")
    public DichVu update(@PathVariable Integer id, @RequestBody DichVu dv) {
        dv.setId_dich_vu(id);
        return dichVuRepository.save(dv);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        dichVuRepository.deleteById(id);
    }
}
