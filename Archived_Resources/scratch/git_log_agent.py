import subprocess

try:
    # Run git log on .agent folder
    result = subprocess.run(["git", "log", "--oneline", "--", ".agent"], capture_output=True, text=True, check=True)
    with open("scratch/git_log_agent.txt", "w", encoding="utf-8") as f:
        f.write(result.stdout)
    print("Success")
except Exception as e:
    with open("scratch/git_log_agent.txt", "w", encoding="utf-8") as f:
        f.write(str(e))
    print("Error:", e)
