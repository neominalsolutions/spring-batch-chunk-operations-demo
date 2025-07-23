package com.mertalptekin.springbatchchunkoperationsdemo.batch;

import com.mertalptekin.springbatchchunkoperationsdemo.model.Hakedis;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class HakedisRowMapper  implements RowMapper<Hakedis> {

    @Override
    public Hakedis mapRow(ResultSet rs, int rowNum) throws SQLException {

        Hakedis hakedis = new Hakedis();
        hakedis.setCalisan_id(rs.getInt("calisan_id"));
        hakedis.setAy(rs.getInt("ay"));
        hakedis.setYil(rs.getInt("yil"));
        hakedis.setToplam_prim(rs.getBigDecimal("toplam_prim"));
        hakedis.setToplam_kesinti(rs.getBigDecimal("toplam_kesinti"));
        hakedis.setToplam_avans(rs.getBigDecimal("toplam_avans"));
        hakedis.setToplam_kazanc(rs.getBigDecimal("toplam_kazanc"));
        hakedis.setHesaplama_tarihi(rs.getDate("hesaplama_tarihi"));

        return hakedis;
    }
}
