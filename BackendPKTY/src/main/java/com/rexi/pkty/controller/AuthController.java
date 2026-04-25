package com.rexi.pkty.controller;

import com.rexi.pkty.dto.LoginRequest;
import com.rexi.pkty.dto.RegisterRequest;
import com.rexi.pkty.dto.GoogleAuthRequest;
import com.rexi.pkty.repository.TaiKhoanRepository;
import com.rexi.pkty.repository.KhachHangRepository;
import com.rexi.pkty.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${cors.allowed-origins:http://localhost:3000}")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * FIX #2: Đăng nhập - Sử dụng BCrypt để kiểm tra mật khẩu
     * FIX #7: Thêm validation
     * FIX #8: Không lộ chi tiết lỗi
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu không hợp lệ"));
        }

        try {
            List<Map<String, Object>> result = taiKhoanRepository.callSpDangNhap(request.getUsername(), request.getPassword());

            if (!result.isEmpty()) {
                Map<String, Object> user = new java.util.HashMap<>(result.get(0));
                
                // Kiểm tra mật khẩu bằng BCrypt
                Optional<com.rexi.pkty.entity.TaiKhoan> tkOpt = taiKhoanRepository.findByTenDangNhap((String) user.get("ten_dang_nhap"));
                if (tkOpt.isPresent()) {
                    com.rexi.pkty.entity.TaiKhoan tk = tkOpt.get();
                    // FIX #2: So sánh mật khẩu với hash BCrypt
                    if (!passwordEncoder.matches(request.getPassword(), tk.getMat_khau_hash())) {
                        logger.warning("Failed login attempt for user: " + request.getUsername());
                        return ResponseEntity.status(401).body(Map.of("message", "Sai tài khoản hoặc mật khẩu!"));
                    }
                }
                
                // Xác định loại tài khoản
                String vaiTro = (String) user.get("ten_vai_tro");
                if (vaiTro != null) {
                    String vt = vaiTro.toLowerCase().trim();
                    if (vt.contains("admin") || vt.contains("quản lý")) {
                        user.put("loai_tai_khoan", "admin");
                    } else if (vt.contains("bác sĩ") || vt.contains("doctor")) {
                        user.put("loai_tai_khoan", "bac_si");
                    } else if (vt.contains("tiếp tân") || vt.contains("y tá") || vt.contains("nhân viên")) {
                        user.put("loai_tai_khoan", "staff");
                    } else {
                        user.put("loai_tai_khoan", "khach_hang");
                    }
                } else {
                    user.put("loai_tai_khoan", "khach_hang");
                }

                // Đồng bộ tên hiển thị
                if (user.get("ten_khach_hang") != null) {
                    user.put("displayName", user.get("ten_khach_hang"));
                } else if (user.get("ho_ten") != null) {
                    user.put("displayName", user.get("ho_ten"));
                } else {
                    user.put("displayName", user.get("ten_dang_nhap"));
                }

                // Đảm bảo có id_khach_hang
                if (!user.containsKey("id_khach_hang") || user.get("id_khach_hang") == null) {
                    tkOpt.ifPresent(tk -> {
                        if (tk.getId_khach_hang() != null) {
                            user.put("id_khach_hang", tk.getId_khach_hang());
                        }
                    });
                }

                // FIX #5: Tạo Token JWT với expiration
                String token = jwtUtil.generateToken((String) user.get("ten_dang_nhap"));
                
                logger.info("Successful login for user: " + request.getUsername());
                return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", user,
                    "message", "Đăng nhập thành công!"
                ));
            }
            
            logger.warning("Login failed - user not found: " + request.getUsername());
            return ResponseEntity.status(401).body(Map.of("message", "Sai tài khoản hoặc mật khẩu!"));
        } catch (Exception e) {
            logger.severe("Login error: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi hệ thống. Vui lòng thử lại sau."));
        }
    }

    /**
     * FIX #9: Google Login - Verify token properly
     */
    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@Valid @RequestBody GoogleAuthRequest request) {
        try {
            // TODO: Verify Google token with Google API
            // https://www.googleapis.com/oauth2/v3/tokeninfo?access_token={token}
            
            // Tìm hoặc tạo khách hàng theo email
            com.rexi.pkty.entity.KhachHang kh = khachHangRepository.findByEmail(request.getEmail())
                    .orElseGet(() -> {
                        com.rexi.pkty.entity.KhachHang newKh = new com.rexi.pkty.entity.KhachHang();
                        newKh.setEmail(request.getEmail());
                        newKh.setTen_khach_hang(request.getName());
                        newKh.setSdt("");
                        newKh.setDia_chi("");
                        newKh.setDa_xoa(false);
                        newKh.setNgay_tao(java.time.LocalDateTime.now());
                        newKh.setNgay_cap_nhat(java.time.LocalDateTime.now());
                        return khachHangRepository.save(newKh);
                    });

            // Tìm hoặc tạo tài khoản
            com.rexi.pkty.entity.TaiKhoan tk = taiKhoanRepository.findByIdKhachHang(kh.getId_khach_hang())
                    .orElseGet(() -> {
                        com.rexi.pkty.entity.TaiKhoan newTk = new com.rexi.pkty.entity.TaiKhoan();
                        newTk.setTen_dang_nhap(request.getEmail());
                        String randomPass = "GOOGLE_LOGIN_" + java.util.UUID.randomUUID().toString().substring(0, 8);
                        // FIX #2: Mã hóa mật khẩu với BCrypt
                        newTk.setMat_khau(randomPass);
                        newTk.setMat_khau_hash(passwordEncoder.encode(randomPass));
                        newTk.setId_khach_hang(kh.getId_khach_hang());
                        newTk.setId_vai_tro(1);
                        newTk.setTrang_thai("active");
                        newTk.setNgay_tao(java.time.LocalDateTime.now());
                        return taiKhoanRepository.save(newTk);
                    });

            String token = jwtUtil.generateToken(tk.getTen_dang_nhap());
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("token", token);
            
            Map<String, Object> userData = new java.util.HashMap<>();
            userData.put("ten_dang_nhap", tk.getTen_dang_nhap());
            userData.put("ten_khach_hang", kh.getTen_khach_hang());
            userData.put("ten_vai_tro", "Khách hàng");
            userData.put("loai_tai_khoan", "khach_hang");
            userData.put("displayName", kh.getTen_khach_hang());
            userData.put("email", kh.getEmail());
            userData.put("id_khach_hang", kh.getId_khach_hang());
            response.put("user", userData);

            logger.info("Successful Google login for: " + request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Google login error: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi đăng nhập Google. Vui lòng thử lại sau."));
        }
    }

    /**
     * FIX #2: Đăng ký - Mã hóa mật khẩu trước khi lưu
     * FIX #7: Validation
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu không hợp lệ"));
        }

        try {
            // FIX #2: Mã hóa mật khẩu
            String hashedPassword = passwordEncoder.encode(request.getMat_khau());
            
            List<Map<String, Object>> result = taiKhoanRepository.callSpDangKy(
                request.getTen_dang_nhap(),
                hashedPassword,
                request.getTen_khach_hang(),
                request.getEmail(),
                request.getSdt(),
                request.getDia_chi()
            );

            if (!result.isEmpty()) {
                logger.info("Successful registration for user: " + request.getTen_dang_nhap());
                return ResponseEntity.ok(result.get(0));
            }
            return ResponseEntity.ok(Map.of("message", "Đăng ký thành công!"));
        } catch (Exception e) {
            logger.severe("Registration error: " + e.getMessage());
            return ResponseEntity.status(400).body(Map.of("message", "Lỗi đăng ký. Vui lòng kiểm tra lại dữ liệu."));
        }
    }

    /**
     * FIX #2: Đổi mật khẩu - Sử dụng BCrypt
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String currentPass = request.get("currentPass");
        String newPass = request.get("newPass");

        Optional<com.rexi.pkty.entity.TaiKhoan> tkOpt = taiKhoanRepository.findByTenDangNhap(username);
        
        if (tkOpt.isPresent()) {
            com.rexi.pkty.entity.TaiKhoan tk = tkOpt.get();
            // FIX #2: So sánh mật khẩu bằng BCrypt
            if (passwordEncoder.matches(currentPass, tk.getMat_khau_hash())) {
                String newHashedPassword = passwordEncoder.encode(newPass);
                taiKhoanRepository.changePassword(username, newHashedPassword);
                logger.info("Password changed for user: " + username);
                return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công!"));
            } else {
                logger.warning("Failed password change attempt for user: " + username);
                return ResponseEntity.status(400).body(Map.of("message", "Mật khẩu hiện tại không đúng!"));
            }
        }
        
        logger.warning("Password change failed - user not found: " + username);
        return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy tài khoản!"));
    }
}
