file_path = r"d:\QLy Phòng Khám Thú Y\Backend\src\main\java\com\rexi\pkty\service\GeminiService.java"

with open(file_path, "r", encoding="utf-8-sig") as f:
    content = f.read()

# Ghi lại dưới dạng UTF-8 không có BOM
with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("STRIPPED BOM SUCCESSFULLY")
