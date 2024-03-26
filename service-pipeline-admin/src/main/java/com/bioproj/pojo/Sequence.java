package com.bioproj.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Sequence {
    @Id
    private String id;
    private String name;
}
