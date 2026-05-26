import urllib.request
import re
import sys
from urllib.parse import quote

# Đảm bảo in được Tiếng Việt
if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

query = "dịch tả lợn châu phi 2026"
url = "https://html.duckduckgo.com/html/"
data = urllib.parse.urlencode({"q": query}).encode("utf-8")

headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Content-Type": "application/x-www-form-urlencoded"
}

req = urllib.request.Request(url, data=data, headers=headers, method="POST")

try:
    with urllib.request.urlopen(req) as response:
        html = response.read().decode('utf-8')
        # Tìm xem có thẻ kết quả tìm kiếm không
        titles = re.findall(r'<a rel="nofollow" class="result__a" href="([^"]+)">([^<]+)</a>', html)
        snippets = re.findall(r'<a class="result__snippet"[^>]*>(.*?)</a>', html)
        
        print(f"SUCCESS: Found {len(titles)} results")
        for i, (link, title) in enumerate(titles[:5]):
            # Loại bỏ thẻ HTML trong snippet nếu có
            snippet = snippets[i] if i < len(snippets) else ""
            snippet = re.sub(r'<[^>]+>', '', snippet).strip()
            
            print(f"\n[{i+1}] Title: {title.strip()}")
            print(f"    URL: {link.strip()}")
            print(f"    Snippet: {snippet}")
            
except Exception as e:
    print("FAILED")
    print(str(e))
