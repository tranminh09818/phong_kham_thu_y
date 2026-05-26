import urllib.request
import json
import sys

api_key = "AIzaSyDs2JPbbzBYo8DZVblMv95P9h00Bdpnxc8"
model_name = "gemini-3.5-flash"
url = f"https://generativelanguage.googleapis.com/v1beta/models/{model_name}:generateContent?key={api_key}"

payload = {
    "contents": [{
        "parts": [{
            "text": "Hello, respond with exactly 3 words."
        }]
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
        print(res_json["candidates"][0]["content"]["parts"][0]["text"])
except Exception as e:
    print("FAILED")
    print(str(e))
    if hasattr(e, 'read'):
        print(e.read().decode('utf-8'))
