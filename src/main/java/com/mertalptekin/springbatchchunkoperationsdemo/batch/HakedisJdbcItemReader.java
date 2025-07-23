package com.mertalptekin.springbatchchunkoperationsdemo.batch;

import com.mertalptekin.springbatchchunkoperationsdemo.model.Hakedis;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class HakedisJdbcItemReader implements ItemReader<Hakedis> {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private HakedisRowMapper rowMapper;
    private Iterator<Hakedis> iterator;
    private List<Hakedis> results;

    @Override
    public Hakedis read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(results==null)
        {
            System.out.println("veri çekilid");
            loadData();
        }

        if(iterator.hasNext()){
            return  iterator.next();
        }
        else {
            return null;
        }
    }

    private void loadData() {
        String sql = "select * from hakedis";
        results = jdbcTemplate.query(sql, rowMapper);
        iterator = results.iterator();
    }
}
