package com.bondlic.bondsearch.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Bond {
    private String isin;
    private String name;
}
