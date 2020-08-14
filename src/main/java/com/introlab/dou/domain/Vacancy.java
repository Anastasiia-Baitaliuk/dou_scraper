package com.introlab.dou.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Vacancy {
    private String title;
    private String location;
    private String company;
    private String shortDescription;
    private String link;
}
