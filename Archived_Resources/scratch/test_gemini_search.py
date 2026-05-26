import urllib.request
import json

api_key = "AIzaSyDOu-eYrg_0aucQSgL9T1C7mOdohNsh7Z0"
model_name = "gemini-3.5-flash"
url = f"https://generativelanguage.googleapis.com/v1beta/models/{model_name}:generateContent?key={api_key}"

# Payload cấu hình Google Search grounding (tools)
payload = {
    "contents": [{
        "parts": [{
            "text": "Ai là đương kim tổng thống Mỹ và thời gian hiện tại là năm nào?"
        }]
    }],
    "tools": [{
        "googleSearch": {}
    }]
}

headers = {
    "Content-Type": "application/json"
}

req = urllib.request.Request(url, data=json.dumps(payload).encode('utf-8'), headers=headers, method="POST")

try:
    with urllib.request.urlopen(req) as response:
        res_data = response.read().decode('utf-8')
        res_json = json.loads(res_data)
        print("SUCCESS")
        print("Reply:")
        print(res_json["candidates"][0]["content"]["parts"][0]["text"])
        
        # Xem metadata của kết quả tìm kiếm nếu có
        grounding_metadata = res_json["candidates"][0].get("groundingMetadata", {})
        if grounding_metadata:
            print("\nGrounding Metadata (Web Search Sources):")
            print(json.dumps(grounding_metadata, indent=2, ensure_ascii=False))
except Exception as e:
    print("FAILED")
    print(str(e))
    if hasattr(e, 'read'):
        print(e.read().decode('utf-8'))
