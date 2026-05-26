import os

backend_dir = r"d:\QLy Phòng Khám Thú Y\Backend"

cleaned_files = []
for root, dirs, files in os.walk(backend_dir):
    for file in files:
        if file.endswith(".java"):
            file_path = os.path.join(root, file)
            try:
                # Đọc file nhạy cảm với BOM
                with open(file_path, "rb") as f:
                    raw_data = f.read(3)
                
                # Check BOM (EF BB BF)
                if raw_data == b'\xef\xbb\xbf':
                    with open(file_path, "r", encoding="utf-8-sig") as f:
                        content = f.read()
                    with open(file_path, "w", encoding="utf-8") as f:
                        f.write(content)
                    cleaned_files.append(file_path)
            except Exception as e:
                print(f"Error reading {file}: {e}")

print(f"CLEANED {len(cleaned_files)} FILES:")
for cf in cleaned_files:
    print(cf)
