def search_inserts():
    try:
        with open(r'd:\QLy Phòng Khám Thú Y\PhongKhamThuY.sql', 'r', encoding='utf-16', errors='ignore') as f:
            content = f.read()
            
        lines = content.split('\n')
        
        output_lines = []
        
        # Search for inserts into HoaDon
        output_lines.append("--- HOA DON INSERTS ---")
        hd_inserts = [line for line in lines if "INSERT" in line.upper() and ("HOADON" in line.upper() or "HOA_DON" in line.upper())]
        for hd in hd_inserts[:20]:
            clean = hd.strip().encode('ascii', 'ignore').decode('ascii')
            output_lines.append(clean[:300])
            
        # Search for inserts into LichHen
        output_lines.append("\n--- LICH HEN INSERTS ---")
        lh_inserts = [line for line in lines if "INSERT" in line.upper() and ("LICHHEN" in line.upper() or "LICH_HEN" in line.upper())]
        for lh in lh_inserts[:20]:
            clean = lh.strip().encode('ascii', 'ignore').decode('ascii')
            output_lines.append(clean[:300])
            
        with open(r'd:\QLy Phòng Khám Thú Y\scratch\inserts_info.txt', 'w', encoding='utf-8') as out_f:
            out_f.write("\n".join(output_lines))
            
        print("Success! Created scratch/inserts_info.txt")
        
    except Exception as e:
        print("Error:", e)

search_inserts()
