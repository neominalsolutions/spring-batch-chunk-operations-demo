CREATE TABLE calisanlar (
    calisan_id SERIAL PRIMARY KEY,
    ad VARCHAR(100),
    soyad VARCHAR(100),
    unvan VARCHAR(100)
);

CREATE TABLE gunluk_isler (
    is_id SERIAL PRIMARY KEY,
    calisan_id INT REFERENCES calisanlar(calisan_id),
    is_tarihi DATE,
    is_tanimi TEXT,
    kazanilan_tutar NUMERIC(10, 2)
);

CREATE TABLE prim_kesinti_avans (
    id SERIAL PRIMARY KEY,
    calisan_id INT REFERENCES calisanlar(calisan_id),
    tarih DATE,
    tur VARCHAR(20) CHECK (tur IN ('prim', 'kesinti', 'avans')),
    aciklama TEXT,
    tutar NUMERIC(10, 2)
);


INSERT INTO prim_kesinti_avans (calisan_id, tarih, tur, aciklama, tutar)
SELECT 
    c.calisan_id,
    DATE_TRUNC('month', CURRENT_DATE) - INTERVAL '1 month' * (m.ay_index - 1) AS tarih,
    t.tur,
    '' AS aciklama,  -- Boş açıklama
    CASE t.tur
        WHEN 'prim' THEN random() * (100000 - 80000) + 80000    
        WHEN 'avans' THEN random() * (10000 - 5000) + 5000  
        WHEN 'kesinti' THEN random() * (3000 - 1000) + 1000   
    END AS tutar
FROM 
    calisanlar c,                             -- calisanlar tablosu
    generate_series(1, 12) AS m(ay_index),    -- 12 ay
    (VALUES ('prim'), ('avans'), ('kesinti')) AS t(tur)  -- her ay için 3 tür
ORDER BY c.calisan_id, m.ay_index, t.tur;


   
 CREATE TABLE hakedis (
    id SERIAL PRIMARY KEY,
    calisan_id INT NOT NULL,        -- Çalışanın ID'si
    toplam_prim DECIMAL(10, 2),            -- O ay için toplam prim
    toplam_kesinti DECIMAL(10, 2),         -- O ay için toplam kesinti
    toplam_avans DECIMAL(10, 2),           -- O ay için toplam avans
    toplam_kazanc DECIMAL(10, 2),   -- O ay için toplam kazanç (prim - kesinti + avans)
    hesaplama_tarihi DATE,          -- Hesaplama yapılma tarihi (ay ve yıl bilgisi buradan alınabilir)
    ay INT,                         -- Hakedişin ait olduğu ay
    yil INT,                        -- Hakedişin ait olduğu yıl
    FOREIGN KEY (calisan_id) REFERENCES calisanlar(calisan_id) -- Çalışanlar tablosu ile ilişki
)

drop table hakedis

-- aylık hakedis hesaplama

SELECT 
    p.calisan_id,  -- Çalışan ID'si
    EXTRACT(MONTH FROM p.tarih) AS ay,  -- Aylık veriler
    EXTRACT(YEAR FROM p.tarih) AS yil,  -- Yıl bazında
    SUM(CASE WHEN p.tur = 'prim' THEN p.tutar ELSE 0 END) AS toplam_prim,  -- Prim toplamı
    SUM(CASE WHEN p.tur = 'kesinti' THEN p.tutar ELSE 0 END) AS toplam_kesinti,  -- Kesinti toplamı
    SUM(CASE WHEN p.tur = 'avans' THEN p.tutar ELSE 0 END) AS toplam_avans,  -- Avans toplamı
    SUM(CASE WHEN p.tur = 'prim' THEN p.tutar ELSE 0 END) -
    SUM(CASE WHEN p.tur = 'kesinti' THEN p.tutar ELSE 0 END) +
    SUM(CASE WHEN p.tur = 'avans' THEN p.tutar ELSE 0 END) AS toplam_kazanc,  -- Toplam kazanç (Prim - Kesinti + Avans)
    CURRENT_DATE AS hesaplama_tarihi  -- Hesaplama tarihi (sorgunun çalıştığı gün)
FROM 
    prim_kesinti_avans p
WHERE
    EXTRACT(MONTH FROM p.tarih) = EXTRACT(MONTH FROM CURRENT_DATE)  -- Bu ayı al
    AND EXTRACT(YEAR FROM p.tarih) = EXTRACT(YEAR FROM CURRENT_DATE)  -- Bu yıl için
GROUP BY 
    p.calisan_id,  -- Çalışan bazında grupla
    EXTRACT(MONTH FROM p.tarih),  -- Ay bazında grupla
    EXTRACT(YEAR FROM p.tarih)   -- Yıl bazında grupla
ORDER BY 
    p.calisan_id


    
   
