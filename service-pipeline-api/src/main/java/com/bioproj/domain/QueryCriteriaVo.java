package com.bioproj.domain;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "QueryCriteriaVo", description = "公共查询条件封装")
public class QueryCriteriaVo {

    private String name;

    private String searchType;

    private Object searchVal;
}
