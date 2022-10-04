package com.bondlic.bondsearch.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Bond {

    private String isin;
    private String name;
}
