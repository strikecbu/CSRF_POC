package com.bondlic.bondsearch.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchCondition {
    private List<String> keyWords;
    private String type;
}
