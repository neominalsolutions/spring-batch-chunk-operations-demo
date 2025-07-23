package com.mertalptekin.springbatchchunkoperationsdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Hakedis {
    private Integer calisan_id;
    private BigDecimal toplam_prim; // aylık toplam prim
    private BigDecimal toplam_kesinti;
    private BigDecimal toplam_avans;
    private BigDecimal toplam_kazanc;
    private Date hesaplama_tarihi;
    private Integer ay;
    private Integer yil;
}
