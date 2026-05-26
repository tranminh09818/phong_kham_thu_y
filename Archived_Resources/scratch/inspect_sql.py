import re

def inspect_sql():
    try:
        with open(r'd:\QLy Phòng Khám Thú Y\PhongKhamThuY.sql', 'r', encoding='utf-16', errors='ignore') as f:
            content = f.read()
            print("Total size:", len(content))
            
            # 1. Tìm cấu trúc bảng LichHen và HoaDon
            print("\n--- CAU TRUC BANG ---")
            lines = content.split('\n')
            
            # Print table creation blocks
            in_table = False
            current_table = ""
            for line in lines:
                if "CREATE TABLE" in line.upper():
                    in_table = True
                    current_table = line
                    print("\n" + line)
                elif in_table:
                    print(line)
                    if ")" in line and ";" in line:
                        in_table = False
                        
            # 2. Tìm các dòng INSERT liên quan đến LichHen và HoaDon
            print("\n--- LICH HEN INSERTS ---")
            lh_inserts = [line for line in lines if "dbo.LichHen" in line or "INSERT INTO LichHen" in line or "INSERT LichHen" in line]
            for lh in lh_inserts[:10]:
                print(lh[:300])
                
            print("\n--- HOA DON INSERTS ---")
            hd_inserts = [line for line in lines if "dbo.HoaDon" in line or "INSERT INTO HoaDon" in line or "INSERT HoaDon" in line]
            for hd in hd_inserts[:10]:
                print(hd[:300])
                
            print("\n--- THU CUNG INSERTS ---")
            tc_inserts = [line for line in lines if "dbo.ThuCung" in line or "INSERT INTO ThuCung" in line or "INSERT ThuCung" in line]
            for tc in tc_inserts[:10]:
                print(tc[:300])
                
    except Exception as e:
        print("Error:", e)

inspect_sql()
