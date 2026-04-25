# 🐾 REXI - PHÒNG KHÁM THÚ Y MANAGEMENT SYSTEM

Hệ thống quản lý phòng khám thú y hiện đại, tích hợp AI chatbot, đặt lịch hẹn, quản lý hồ sơ bệnh án.

![Status](https://img.shields.io/badge/status-production--ready-brightgreen)
![Docker](https://img.shields.io/badge/docker-ready-blue)
![License](https://img.shields.io/badge/license-MIT-green)

---

## 🌟 FEATURES

### 👥 Quản Lý Khách Hàng
- ✅ Đăng ký / Đăng nhập (Password hashing với BCrypt)
- ✅ Google OAuth integration
- ✅ Quản lý profile
- ✅ Quản lý thú cưng

### 📅 Đặt Lịch Hẹn
- ✅ Đặt lịch khám trực tuyến
- ✅ Xem lịch sử lịch hẹn
- ✅ Xác nhận lịch hẹn

### 🏥 Quản Lý Hồ Sơ Bệnh Án
- ✅ Tạo hồ sơ bệnh án
- ✅ Ghi nhận triệu chứng, chẩn đoán
- ✅ Đơn thuốc, hướng dẫn chăm sóc
- ✅ Xét nghiệm & kết quả

### 💳 Quản Lý Tài Chính
- ✅ Tạo hóa đơn
- ✅ Theo dõi thanh toán
- ✅ Báo cáo doanh thu

### 🤖 AI CHATBOT
- ✅ Groq AI (Text - nhanh)
- ✅ Gemini AI (Multimodal - hình ảnh, video)
- ✅ Tư vấn sức khỏe thú cưng 24/7

### 👨‍💼 Quản Lý Admin
- ✅ Dashboard thống kê
- ✅ Quản lý nhân viên & phân quyền
- ✅ Quản lý dịch vụ
- ✅ Quản lý kho thuốc
- ✅ Báo cáo & thống kê

---

## 🛠️ TECH STACK

### Backend
- **Java 17** + Spring Boot 3.5.13
- **SQL Server** 2022
- **Spring Security** + JWT
- **Spring Data JPA** (Hibernate)
- **Groq AI** + **Google Gemini** API

### Frontend
- **React 18** + TypeScript
- **Vite** (bundler)
- **Axios** (API client)
- **React Router** v6
- **Google OAuth**

### DevOps
- **Docker** + Docker Compose
- **Nginx** (reverse proxy)
- **Multi-stage builds** (optimized images)

---

## 🚀 QUICK START

### Yêu Cầu
- Docker + Docker Compose
- Git

### 3 Bước Setup

```bash
# 1. Clone project
git clone <repo-url>
cd rexi-clinic

# 2. Tạo environment files
cp BackendPKTY/.env.example BackendPKTY/.env
cp "QLy Phòng Khám Thú Y/.env.example" "QLy Phòng Khám Thú Y/.env.local"

# 3. Chạy Docker
docker-compose up --build
```

**Truy cập**:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8081
- Database: localhost:1433

👉 **[Xem chi tiết tại SETUP_GUIDE.md](./SETUP_GUIDE.md)**

---

## 📊 PROJECT STRUCTURE

```
rexi-clinic/
├── BackendPKTY/                    # Spring Boot Backend
│   ├── src/main/java/com/rexi/pkty/
│   │   ├── controller/             # REST endpoints
│   │   ├── service/                # Business logic
│   │   ├── repository/             # Data access (JPA)
│   │   ├── entity/                 # Database models
│   │   ├── dto/                    # Data transfer objects
│   │   ├── config/                 # Configuration
│   │   └── security/               # JWT, Auth
│   ├── Dockerfile
│   ├── pom.xml
│   └── .env.example
│
├── QLy Phòng Khám Thú Y/           # React Frontend
│   ├── src/
│   │   ├── pages/                  # Page components
│   │   ├── components/             # Reusable components
│   │   ├── services/               # API services, axios config
│   │   ├── contexts/               # React Context
│   │   ├── hooks/                  # Custom hooks
│   │   ├── layouts/                # Layout components
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── Dockerfile
│   ├── nginx.conf
│   └── .env.example
│
├── docker-compose.yml              # Orchestration
├── .gitignore
├── SETUP_GUIDE.md                  # Setup instructions
└── FIX_REPORT_TIENG_VIET.md        # Detailed fix report
```

---

## 🔐 SECURITY FEATURES

✅ **Password Hashing**: BCrypt (adaptive salt)  
✅ **JWT Authentication**: 24-hour expiration  
✅ **Input Validation**: @NotBlank, @Email, @Size, etc.  
✅ **CORS**: Whitelist specific domains  
✅ **Environment Variables**: No hardcoded secrets  
✅ **Error Handling**: Generic messages to clients, detailed logs  
✅ **Transaction Management**: Atomic database operations  
✅ **SQL Injection Protection**: Parameterized queries (JPA)  

---

## 📈 API ENDPOINTS

### Authentication
```
POST   /api/auth/login              - Đăng nhập
POST   /api/auth/register           - Đăng ký
POST   /api/auth/google-login       - Google OAuth
POST   /api/auth/change-password    - Đổi mật khẩu
```

### Appointments
```
GET    /api/lich-hen                - Tất cả lịch hẹn (admin)
GET    /api/lich-hen/hom-nay        - Lịch hôm nay
GET    /api/lich-hen/khach/{id}     - Lịch của khách hàng
POST   /api/lich-hen                - Đặt lịch mới
GET    /api/lich-hen/count          - Đếm tổng lịch
```

### Medical Records
```
POST   /api/benh-an                 - Tạo hồ sơ bệnh án
GET    /api/xet-nghiem/{id_ho_so}   - Lấy kết quả xét nghiệm
```

### Chat (AI)
```
POST   /api/chat                    - Chat với AI (Groq/Gemini)
```

---

## 🐛 TROUBLESHOOTING

### Port conflict
```bash
# Change port in docker-compose.yml
# "3000:3000" → "3001:3000"
docker-compose up --build
```

### Database connection error
```bash
docker logs rexi_sqlserver
docker-compose restart sqlserver
```

### API connection failed
```bash
docker logs rexi_backend
# Check CORS settings in application.properties
```

### Full reset
```bash
docker-compose down -v
docker system prune -a
docker-compose up --build
```

---

## 📚 DOCUMENTATION

- **[SETUP_GUIDE.md](./SETUP_GUIDE.md)** - Hướng dẫn setup chi tiết
- **[FIX_REPORT_TIENG_VIET.md](./FIX_REPORT_TIENG_VIET.md)** - 18 lỗi đã sửa
- **[SUMMARY_FIX.md](./SUMMARY_FIX.md)** - Tóm tắt lỗi & fix

---

## 🔄 DATABASE SCHEMA

Main tables:
- `khach_hang` - Customers
- `tai_khoan` - Accounts
- `thu_cung` - Pets
- `lich_hen` - Appointments
- `ho_so_benh_an` - Medical records
- `dich_vu` - Services
- `hoa_don` - Invoices
- `nhan_vien` - Staff
- `vai_tro` - Roles

---

## 👥 TEAM ROLES

- **Admin** - Quản lý toàn hệ thống
- **Bác sĩ** - Tạo hồ sơ bệnh án, chẩn đoán
- **Tiếp tân** - Quản lý lịch hẹn
- **Nhân viên kho** - Quản lý kho thuốc
- **Khách hàng** - Đặt lịch, xem hồ sơ

---

## 🚀 PRODUCTION CHECKLIST

- [ ] Đặt strong JWT secret (>32 chars)
- [ ] Update database connection string
- [ ] Thêm HTTPS certificate
- [ ] Set CORS allowed origins
- [ ] Enable logging
- [ ] Setup database backups
- [ ] Configure email notifications (TODO)
- [ ] Add rate limiting (TODO)
- [ ] Enable 2FA (TODO)

---

## 📝 FUTURE FEATURES (TODO)

- [ ] Password reset via email
- [ ] Two-factor authentication
- [ ] Push notifications
- [ ] PDF export (invoices, medical records)
- [ ] Advanced search & filtering
- [ ] Analytics dashboard
- [ ] Payment gateway integration
- [ ] SMS reminders

---

## 🤝 CONTRIBUTING

1. Create feature branch: `git checkout -b feature/name`
2. Commit changes: `git commit -m "Add feature"`
3. Push: `git push origin feature/name`
4. Open Pull Request

---

## 📄 LICENSE

MIT License - feel free to use in your projects

---

## 📞 SUPPORT

Gặp vấn đề?
- Xem [SETUP_GUIDE.md](./SETUP_GUIDE.md) Troubleshooting section
- Check Docker logs: `docker logs <container_name>`
- Review [FIX_REPORT_TIENG_VIET.md](./FIX_REPORT_TIENG_VIET.md)

---

**Made with ❤️ by Gordon - Docker AI Assistant**

*Last updated: 2024*
