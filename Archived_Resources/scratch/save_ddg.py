import urllib.request
from urllib.parse import urlencode

query = "dịch tả lợn châu phi 2026"
url = "https://html.duckduckgo.com/html/"
data = urlencode({"q": query}).encode("utf-8")

headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Content-Type": "application/x-www-form-urlencoded"
}

req = urllib.request.Request(url, data=data, headers=headers, method="POST")

try:
    with urllib.request.urlopen(req) as response:
        html = response.read().decode('utf-8')
        with open("d:\\QLy Phòng Khám Thú Y\\scratch/ddg_response.html", "w", encoding="utf-8") as f:
            f.write(html)
        print("SAVED SUCCESSFULLY")
except Exception as e:
    print(f"FAILED: {e}")
