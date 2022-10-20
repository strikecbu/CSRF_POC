package com.bondlic.bondsearch.model;

import lombok.Data;

import java.util.List;

@Data
public class CombineModel {
    private List<String> names;
    private Long fileSize;
}
