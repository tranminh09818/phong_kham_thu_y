"""
Tạo ảnh thumbnail WebP (480x270, tỷ lệ 16:9) từ video meonhayfooter.mp4
Sử dụng Playwright headless browser để render frame ở giây thứ 8.
"""
import http.server
import threading
import os
import base64
import time

VIDEO_PATH = r"D:\QLy Phòng Khám Thú Y\Frontend\public\img\meonhayfooter.mp4"
OUT_PATH   = r"D:\QLy Phòng Khám Thú Y\Frontend\public\img\meonhayfooter_thumb.webp"
SERVE_DIR  = r"D:\QLy Phòng Khám Thú Y\Frontend\public"
PORT       = 9988

# --- Khởi động HTTP server đơn giản ---
def start_server():
    os.chdir(SERVE_DIR)
    handler = http.server.SimpleHTTPRequestHandler
    httpd = http.server.HTTPServer(("127.0.0.1", PORT), handler)
    httpd.serve_forever()

thread = threading.Thread(target=start_server, daemon=True)
thread.start()
time.sleep(1)  # Đợi server sẵn sàng

# --- HTML lấy frame tại giây 8 ---
HTML = f"""<!DOCTYPE html>
<html>
<body style="margin:0;background:#000;">
  <video id="v" src="http://127.0.0.1:{PORT}/img/meonhayfooter.mp4"
         muted crossorigin="anonymous" style="display:none;"></video>
  <canvas id="c" width="480" height="270"></canvas>
  <script>
    const v = document.getElementById('v');
    const c = document.getElementById('c');
    const ctx = c.getContext('2d');
    v.addEventListener('loadedmetadata', () => {{ v.currentTime = 8; }});
    v.addEventListener('seeked', () => {{
      ctx.drawImage(v, 0, 0, 480, 270);
      window._done = c.toDataURL('image/webp', 0.88);
    }});
    v.load();
  </script>
</body>
</html>"""

from playwright.sync_api import sync_playwright

with sync_playwright() as p:
    browser = p.chromium.launch()
    page = browser.new_page(viewport={"width": 490, "height": 280})
    page.set_content(HTML)
    # Đợi video seek xong
    page.wait_for_function("window._done !== undefined", timeout=20000)
    b64 = page.evaluate("window._done")
    browser.close()

# --- Lưu WebP ---
if b64:
    header, encoded = b64.split(",", 1)
    img_bytes = base64.b64decode(encoded)
    with open(OUT_PATH, "wb") as f:
        f.write(img_bytes)
    size_kb = len(img_bytes) / 1024
    print(f"✅ Đã lưu thumbnail WebP: {OUT_PATH}")
    print(f"   Kích thước: {size_kb:.1f} KB")
else:
    print("❌ Không lấy được frame từ video.")
