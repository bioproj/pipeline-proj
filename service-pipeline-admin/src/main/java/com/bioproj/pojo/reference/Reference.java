package com.bioproj.pojo.reference;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Reference {

    @Id
    private String id;
    private String name;
    private String species;
    private String genomeVersion;
    private String aligner;
    private String alignerVersion;
    private String prefix;
    private Date createTime;

    private String fasta;
    private String gtf;
    private String gff;

}
