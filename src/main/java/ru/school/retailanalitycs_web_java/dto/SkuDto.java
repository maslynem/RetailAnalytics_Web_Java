package ru.school.retailanalitycs_web_java.dto;

import lombok.*;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuDto {
    @CsvBindByName(column = "sku_id", required = true)
    @CsvBindByPosition(position = 0)
    private Integer id;

    @CsvBindByName(column = "sku_name", required = true)
    @CsvBindByPosition(position = 1)
    private String skuName;

    @CsvBindByName(column = "group_id", required = true)
    @CsvBindByPosition(position = 2)
    private Integer skuGroup;
}
