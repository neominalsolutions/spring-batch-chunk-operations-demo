package com.mertalptekin.springbatchchunkoperationsdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCredit {

    private String name;
    private Integer age;
    private Integer creditScore;

}
