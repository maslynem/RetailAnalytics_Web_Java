package ru.school.retailanalitycs_web_java.dto.entityDto.transactionDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.school.retailanalitycs_web_java.dto.entityDto.cardDto.CardReadDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReadDto {
    private Long id;

    private CardReadDto customerCard;

    private Double transactionSum;

    private LocalDateTime transactionDatetime;

    private Long transactionStoreId;
}
