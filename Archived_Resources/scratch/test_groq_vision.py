import urllib.request
import json

api_key = "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"
model_name = "meta-llama/llama-4-scout-17b-16e-instruct"
url = "https://api.groq.com/openai/v1/chat/completions"

payload = {
    "model": model_name,
    "messages": [{
        "role": "user",
        "content": "Hello, respond with exactly 3 words."
    }]
}

headers = {
    "Content-Type": "application/json",
    "Authorization": f"Bearer {api_key}",
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
}

req = urllib.request.Request(url, data=json.dumps(payload).encode('utf-8'), headers=headers, method="POST")

try:
    with urllib.request.urlopen(req) as response:
        res_data = response.read().decode('utf-8')
        res_json = json.loads(res_data)
        text = res_json["choices"][0]["message"]["content"]
        print("SUCCESS")
        print(text)
except Exception as e:
    print("FAILED")
    print(str(e))
    if hasattr(e, 'read'):
        print(e.read().decode('utf-8'))
