# 🚀 HƯỚNG DẪN SETUP REXI - PHÒNG KHÁM THÚ Y

**Dành cho các thành viên trong team hoặc bạn bè**

Tài liệu này hướng dẫn cách setup và chạy project Rexi trên máy của bạn. Chỉ cần 3 bước, tất cả sẽ lên ngay!

---

## 📋 YÊU CẦU

Trước khi bắt đầu, bạn cần cài:

- **Docker** (kèm Docker Compose): https://www.docker.com/products/docker-desktop
- **Git**: https://git-scm.com/

Kiểm tra cài xong chưa:
```bash
docker --version
docker-compose --version
git --version
```

---

## 🎯 3 BƯỚC SETUP

### **Bước 1: Clone Project**

```bash
git clone <link-github-của-bạn>
cd <tên-project>
```

---

### **Bước 2: Tạo Environment Files**

#### 2.1 Backend Environment
```bash
cp BackendPKTY/.env.example BackendPKTY/.env
```

#### 2.2 Frontend Environment
```bash
cp QLy\ Phòng\ Khám\ Thú\ Y/.env.example QLy\ Phòng\ Khám\ Thú\ Y/.env.local
```

#### 2.3 (Optional) Thêm API Keys

Nếu bạn có Groq hoặc Gemini API keys, mở file `BackendPKTY/.env` và thêm:

```env
GROQ_API_KEY=your_api_key_here
GEMINI_API_KEY=your_api_key_here
```

> ⚠️ **KHÔNG COMMIT** `.env` files lên Git! (.gitignore đã config sẵn)

---

### **Bước 3: Chạy Docker**

```bash
docker-compose up --build
```

**Lần đầu sẽ tốn vài phút để download images & build. Hãy chờ!**

Khi thấy log tương tự dưới đây = OK:
```
✓ backend (healthy)
✓ frontend (healthy)
✓ sqlserver (healthy)
```

---

## 🌐 TRUY CẬP

Khi Docker chạy xong:

| Service | URL | Tài khoản test |
|---------|-----|----------------|
| **Frontend** | http://localhost:3000 | Đăng ký tài khoản mới |
| **Backend API** | http://localhost:8081 | - |
| **Database** | localhost:1433 | sa / 123456 |

### Test Login

Tài khoản default (nếu có dữ liệu từ SQL):
- Username: `admin`
- Password: (Phải có trong database)

Hoặc **đăng ký tài khoản mới** tại http://localhost:3000

---

## 📁 FOLDER STRUCTURE

```
rexi-clinic/
├── BackendPKTY/           # Backend (Java + Spring Boot)
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   ├── .env.example
│   └── .env (DO NOT COMMIT)
│
├── QLy Phòng Khám Thú Y/  # Frontend (React + TypeScript)
│   ├── src/
│   ├── package.json
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── .env.example
│   └── .env.local (DO NOT COMMIT)
│
├── docker-compose.yml     # Orchestrate tất cả service
├── .gitignore             # Git ignore config
└── README.md
```

---

## 🐛 TROUBLESHOOTING

### ❌ "Port 3000 already in use"
```bash
# Tìm process đang dùng port 3000
lsof -i :3000

# Kill process (thay 12345 bằng PID)
kill -9 12345

# Hoặc change port trong docker-compose.yml
# "3000:3000" → "3001:3000"
```

### ❌ "Port 8081 already in use"
```bash
lsof -i :8081
kill -9 <PID>
# Hoặc change port trong docker-compose.yml
```

### ❌ "Port 1433 already in use"
```bash
# SQL Server port conflict
# Dừng SQL Server instance khác hoặc change port:
# "1433:1433" → "1434:1433"
```

### ❌ Database connection failed
```bash
# Xem logs database
docker logs rexi_sqlserver

# Restart service
docker-compose restart sqlserver
```

### ❌ Frontend shows blank/API error
```bash
# Check backend logs
docker logs rexi_backend

# Restart
docker-compose restart backend
```

### ❌ Docker build failed
```bash
# Clear cache & rebuild
docker-compose down
docker system prune -a
docker-compose up --build
```

---

## 💻 LOCAL DEVELOPMENT (Không dùng Docker)

Nếu bạn muốn develop local (không Docker):

### Frontend
```bash
cd "QLy Phòng Khám Thú Y"
npm install
npm run dev
# Mở http://localhost:5173
```

### Backend
Bạn cần:
- Java 17+
- Maven
- SQL Server running (hoặc Docker: `docker run -e SA_PASSWORD=123456 -e ACCEPT_EULA=Y -p 1433:1433 mcr.microsoft.com/mssql/server:2022-latest`)

```bash
cd BackendPKTY
mvn clean install
mvn spring-boot:run
# API chạy tại http://localhost:8081
```

---

## 📝 DEVELOPMENT NOTES

### Khi sửa code:

**Frontend**:
- Sửa file `.tsx` → Hot reload tự động
- Không cần restart Docker

**Backend**:
- Sửa file `.java` → Phải rebuild Docker
  ```bash
  docker-compose restart backend
  ```
- Hoặc dùng local development

### Database

File SQL init: `QLy Phòng Khám Thú Y/PKTY.sql`

Database được tạo tự động lần đầu. Nếu muốn reset:
```bash
docker-compose down -v  # -v: delete volumes
docker-compose up --build
```

---

## 🔐 SECURITY NOTES

### 🚨 KHÔNG BAO GIỜ:
- Commit `.env` files
- Commit credentials, API keys
- Commit `.idea/`, `.vscode/`
- Push mật khẩu database

### ✅ LUÔN:
- Dùng `.env` files cho config
- Dùng environment variables
- Review `.gitignore` trước commit

---

## 🚀 PRODUCTION DEPLOYMENT

Khi deploy lên server thực (AWS, Heroku, DigitalOcean, etc.):

1. **Đặt environment variables** trên server:
   ```bash
   export DB_URL=jdbc:sqlserver://your-server...
   export DB_USERNAME=sa
   export DB_PASSWORD=strong_password_here
   export GROQ_API_KEY=your_key
   export GEMINI_API_KEY=your_key
   export JWT_SECRET=very_long_random_string_min_32_chars
   ```

2. **Build images**:
   ```bash
   docker-compose build
   ```

3. **Push lên registry** (Docker Hub, AWS ECR, etc.)

4. **Deploy**:
   ```bash
   docker-compose up -d
   ```

---

## 📞 CẦN GIÚP?

- Xem `FIX_REPORT_TIENG_VIET.md` - Chi tiết các lỗi được sửa
- Xem `SUMMARY_FIX.md` - Tóm tắt nhanh
- Check logs: `docker logs <container_name>`
- Xem code comments: Hầu hết method đều có comment (FIX #X)

---

## ✅ CHECKLIST

- [ ] Cài Docker & Docker Compose
- [ ] Clone project
- [ ] Tạo .env files
- [ ] Chạy `docker-compose up --build`
- [ ] Truy cập http://localhost:3000
- [ ] Đăng ký tài khoản test
- [ ] Kiểm tra Database bằng SQL Server Management Studio

---

**Happy Coding! 🎉**

*Tạo bởi Gordon - Docker AI Assistant*
