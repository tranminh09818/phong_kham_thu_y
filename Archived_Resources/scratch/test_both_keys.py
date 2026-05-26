import urllib.request
import json

keys = [
    "AIzaSyDs2JPbbzBYo8DZVblMv95P9h00Bdpnxc8",
    "AIzaSyDOu-eYrg_0aucQSgL9T1C7mOdohNsh7Z0"
]
model_name = "gemini-3.5-flash"

for idx, api_key in enumerate(keys):
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
            text = res_json["candidates"][0]["content"]["parts"][0]["text"].strip()
            print(f"Key {idx + 1} ({api_key[:8]}...): SUCCESS -> Response: '{text}'")
    except Exception as e:
        print(f"Key {idx + 1} ({api_key[:8]}...): FAILED -> Error: {str(e)}")
        if hasattr(e, 'read'):
            print(e.read().decode('utf-8'))
