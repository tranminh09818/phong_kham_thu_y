import re

def search_inserts_full():
    try:
        with open(r'd:\QLy Phòng Khám Thú Y\PhongKhamThuY.sql', 'r', encoding='utf-16', errors='ignore') as f:
            content = f.read()
            
        output_lines = []
        
        # We will split by statements (using GO or ;)
        statements = content.split('\n')
        
        hd_blocks = []
        lh_blocks = []
        
        current_block = []
        for line in statements:
            current_block.append(line)
            if "GO" in line.upper() or ";" in line:
                block_str = "\n".join(current_block)
                if "INSERT" in block_str.upper():
                    if "HOADON" in block_str.upper() or "HOA_DON" in block_str.upper():
                        hd_blocks.append(block_str)
                    if "LICHHEN" in block_str.upper() or "LICH_HEN" in block_str.upper():
                        lh_blocks.append(block_str)
                current_block = []
                
        output_lines.append("--- HOA DON INSERTS (FIRST 5) ---")
        for b in hd_blocks[:5]:
            clean_b = b.encode('ascii', 'ignore').decode('ascii')
            output_lines.append(clean_b[:800])
            output_lines.append("="*40)
            
        output_lines.append("\n--- LICH HEN INSERTS (FIRST 5) ---")
        for b in lh_blocks[:5]:
            clean_b = b.encode('ascii', 'ignore').decode('ascii')
            output_lines.append(clean_b[:800])
            output_lines.append("="*40)
            
        with open(r'd:\QLy Phòng Khám Thú Y\scratch\inserts_full_info.txt', 'w', encoding='utf-8') as out_f:
            out_f.write("\n".join(output_lines))
            
        print("Success! Created scratch/inserts_full_info.txt")
        
    except Exception as e:
        print("Error:", e)

search_inserts_full()
