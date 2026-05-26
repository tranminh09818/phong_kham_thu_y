import re

def inspect_tables():
    try:
        with open(r'd:\QLy Phòng Khám Thú Y\PhongKhamThuY.sql', 'r', encoding='utf-16', errors='ignore') as f:
            content = f.read()
            
        lines = content.split('\n')
        
        output_lines = []
        
        # 1. Extract CREATE TABLE blocks for LichHen, HoaDon, ThuCung, KhachHang
        target_tables = ["LICHHEN", "HOADON", "THUCUNG", "KHACHHANG"]
        
        current_table = ""
        in_table = False
        table_block = []
        
        for line in lines:
            if "CREATE TABLE" in line.upper():
                # Check if it is one of our target tables
                for t in target_tables:
                    if f"[{t}]" in line.upper() or f" {t} " in line.upper():
                        in_table = True
                        current_table = t
                        table_block = [line]
                        break
            elif in_table:
                table_block.append(line)
                if ")" in line and ";" in line or "GO" in line.upper():
                    in_table = False
                    output_lines.append(f"\n--- TABLE {current_table} ---")
                    output_lines.extend(table_block)
                    table_block = []
                    
        # 2. Extract some sample inserts
        output_lines.append("\n--- SAMPLE INSERTS ---")
        for line in lines:
            if "INSERT" in line.upper():
                for t in target_tables:
                    if f"[{t}]" in line or f" {t} " in line:
                        clean_line = line.strip().encode('ascii', 'ignore').decode('ascii')
                        if len(clean_line) > 0:
                            output_lines.append(clean_line[:300])
                            
        # Write clean output to a temporary UTF-8 file
        with open(r'd:\QLy Phòng Khám Thú Y\scratch\db_structure.txt', 'w', encoding='utf-8') as out_f:
            out_f.write("\n".join(output_lines))
            
        print("Success! Created scratch/db_structure.txt")
        
    except Exception as e:
        print("Error:", e)

inspect_tables()
