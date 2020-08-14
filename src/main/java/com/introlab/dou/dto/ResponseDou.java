package com.introlab.dou.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDou {
    private Integer num;
    private Boolean last;
    private String html;

}