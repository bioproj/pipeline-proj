package com.bioproj.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "PageModel", description = "分页数据")
public class PageModel<T> {

    @ApiModelProperty(value = "分页页码")
    private Integer number;

    @ApiModelProperty(value = "每页数据个数")
    private Integer size;

    @ApiModelProperty(value = "数据总数")
    private Integer count;

    private Integer totalPages;

    @ApiModelProperty(value = "数据列表")
    private List<T> content;

    public Integer getTotalPages() {
        if (this.getCount() == null) {
            this.setCount(0);
        }
        if (this.getSize() == null) {
            this.setSize(10);
        }
        int pages = this.getCount() / this.getSize();
        int mol = this.getCount() % this.getSize();
        if (mol > 0) {
            return pages + 1;
        }
        return pages;
    }

}
