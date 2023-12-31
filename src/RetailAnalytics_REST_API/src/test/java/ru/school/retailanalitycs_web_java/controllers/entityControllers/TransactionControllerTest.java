package ru.school.retailanalitycs_web_java.controllers.entityControllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.school.retailanalitycs_web_java.IntegrationTestBase;
import ru.school.retailanalitycs_web_java.IntegrationsTestConfiguration;
import ru.school.retailanalitycs_web_java.dto.entityDto.cardDto.CardReadDto;
import ru.school.retailanalitycs_web_java.dto.entityDto.transactionDto.TransactionCreateDto;
import ru.school.retailanalitycs_web_java.dto.entityDto.transactionDto.TransactionReadDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.Month.SEPTEMBER;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.school.retailanalitycs_web_java.exceptions.ExceptionCode.ENTITY_IS_NOT_VALID;
import static ru.school.retailanalitycs_web_java.exceptions.ExceptionCode.NOT_FOUND;

@SpringBootTest(classes = IntegrationsTestConfiguration.class)
@AutoConfigureMockMvc
@Transactional
class TransactionControllerTest extends IntegrationTestBase {
    private static final Long TRANSACTION_ID = 4L;
    private static final Long NOT_EXISTING_TRANSACTION_ID = 1000L;
    private static final Long NOT_EXISTING_CARD_ID = 1000L;
    private static final Long NOT_EXISTING_STORE_ID = 1000L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAllTransactions() throws Exception {
        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk());
    }

    @Test
    void findTransactionsBy_page_1_size_2() throws Exception {

        TransactionReadDto first = TransactionReadDto.builder()
                .id(3L)
                .customerCard(getCardReadDtoWithId(17L))
                .transactionSum(294.7881466)
                .transactionDatetime(LocalDateTime.of(LocalDate.of(2019, SEPTEMBER, 25), LocalTime.of(9, 52, 52)))
                .transactionStoreId(1L)
                .build();
        TransactionReadDto second = TransactionReadDto.builder()
                .id(4L)
                .customerCard(getCardReadDtoWithId(13L))
                .transactionSum(79.97275934)
                .transactionDatetime(LocalDateTime.of(LocalDate.of(2021, SEPTEMBER, 15), LocalTime.of(15, 52, 53)))
                .transactionStoreId(2L)
                .build();
        mockMvc.perform(get("/api/v1/transactions?page=1&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].id").value(first.getId()))
                .andExpect(jsonPath("content[0].customerCard.id").value(first.getCustomerCard().getId()))
                .andExpect(jsonPath("content[0].transactionSum").value(first.getTransactionSum()))
                .andExpect(jsonPath("content[0].transactionDatetime").exists())
                .andExpect(jsonPath("content[0].transactionStoreId").value(first.getTransactionStoreId()))
                .andExpect(jsonPath("content[1].id").value(second.getId()))
                .andExpect(jsonPath("content[1].customerCard.id").value(second.getCustomerCard().getId()))
                .andExpect(jsonPath("content[1].transactionSum").value(second.getTransactionSum()))
                .andExpect(jsonPath("content[1].transactionDatetime").exists())
                .andExpect(jsonPath("content[1].transactionStoreId").value(second.getTransactionStoreId()))

                .andExpect(jsonPath("pageable.pageNumber").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(2))
                .andExpect(jsonPath("$.totalPages").value(100))
                .andExpect(jsonPath("$.totalElements").value(200));
    }

    @Test
    void findTransactionById() throws Exception {
        TransactionReadDto transactionDto = TransactionReadDto.builder()
                .customerCard(getCardReadDtoWithId(13L))
                .transactionSum(79.97275934)
                .transactionDatetime(LocalDateTime.of(LocalDate.of(2021, SEPTEMBER, 15), LocalTime.of(15, 52, 53)))
                .transactionStoreId(2L)
                .build();
        mockMvc.perform(get("/api/v1/transactions/{TRANSACTION_ID}", TRANSACTION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TRANSACTION_ID))
                .andExpect(jsonPath("$.customerCard.id").value(transactionDto.getCustomerCard().getId()))
                .andExpect(jsonPath("$.transactionSum").value(transactionDto.getTransactionSum()))
                .andExpect(jsonPath("$.transactionDatetime").exists())
                .andExpect(jsonPath("$.transactionStoreId").value(transactionDto.getTransactionStoreId()));
    }

    @Test
    void findNotExistingTransaction() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/{NOT_EXISTING_TRANSACTION_ID}", NOT_EXISTING_TRANSACTION_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(NOT_FOUND.name()))
                .andExpect(jsonPath("$.messages").exists());
    }

    @Test
    void update() throws Exception {
        TransactionCreateDto transactionDto = TransactionCreateDto.builder()
                .customerCard(13L)
                .transactionSum(1.0)
                .transactionDatetime(LocalDateTime.now())
                .transactionStoreId(2L)
                .build();
        String requestJson = objectMapper.writeValueAsString(transactionDto);
        mockMvc.perform(put("/api/v1/transactions/" + TRANSACTION_ID)
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TRANSACTION_ID))
                .andExpect(jsonPath("$.customerCard.id").value(transactionDto.getCustomerCard()))
                .andExpect(jsonPath("$.transactionSum").value(transactionDto.getTransactionSum()))
                .andExpect(jsonPath("$.transactionDatetime").exists())
                .andExpect(jsonPath("$.transactionStoreId").value(transactionDto.getTransactionStoreId()));
    }

    @Test
    void updateNotExistingTransaction_shouldReturnNotFound() throws Exception {
        TransactionCreateDto transactionDto = TransactionCreateDto.builder()
                .customerCard(13L)
                .transactionSum(1.0)
                .transactionDatetime(LocalDateTime.now())
                .transactionStoreId(2L)
                .build();
        String requestJson = objectMapper.writeValueAsString(transactionDto);
        mockMvc.perform(put("/api/v1/transactions/" + NOT_EXISTING_TRANSACTION_ID)
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(NOT_FOUND.name()))
                .andExpect(jsonPath("$.messages").exists());
    }

    @Test
    void updateWithNotExistingStoreID_shouldReturnNotFound() throws Exception {
        TransactionCreateDto transactionDto = TransactionCreateDto.builder()
                .customerCard(13L)
                .transactionSum(1.0)
                .transactionDatetime(LocalDateTime.now())
                .transactionStoreId(NOT_EXISTING_STORE_ID)
                .build();
        String requestJson = objectMapper.writeValueAsString(transactionDto);
        mockMvc.perform(put("/api/v1/transactions/" + TRANSACTION_ID)
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(NOT_FOUND.name()))
                .andExpect(jsonPath("$.messages").exists());
    }

    @Test
    void updateWithNotExistingCustomerCard_shouldReturnNotFound() throws Exception {
        TransactionCreateDto transactionDto = TransactionCreateDto.builder()
                .customerCard(NOT_EXISTING_CARD_ID)
                .transactionSum(1.0)
                .transactionDatetime(LocalDateTime.now())
                .transactionStoreId(2L)
                .build();
        String requestJson = objectMapper.writeValueAsString(transactionDto);
        mockMvc.perform(put("/api/v1/transactions/" + TRANSACTION_ID)
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(NOT_FOUND.name()))
                .andExpect(jsonPath("$.messages").exists());
    }

    @Test
    void create() throws Exception {
        TransactionCreateDto transactionDto = TransactionCreateDto.builder()
                .customerCard(13L)
                .transactionSum(79.97275934)
                .transactionDatetime(LocalDateTime.of(LocalDate.of(2021, SEPTEMBER, 15), LocalTime.of(15, 52, 53)))
                .transactionStoreId(2L)
                .build();
        String requestJson = objectMapper.writeValueAsString(transactionDto);
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(201))
                .andExpect(jsonPath("$.customerCard.id").value(transactionDto.getCustomerCard()))
                .andExpect(jsonPath("$.transactionSum").value(transactionDto.getTransactionSum()))
                .andExpect(jsonPath("$.transactionDatetime").exists())
                .andExpect(jsonPath("$.transactionStoreId").value(transactionDto.getTransactionStoreId()));
    }

    @Test
    void createWithNotExistingStoreID_shouldReturnNotFound() throws Exception {
        TransactionCreateDto transactionDto = TransactionCreateDto.builder()
                .customerCard(13L)
                .transactionSum(79.97275934)
                .transactionDatetime(LocalDateTime.of(LocalDate.of(2021, SEPTEMBER, 15), LocalTime.of(15, 52, 53)))
                .transactionStoreId(NOT_EXISTING_STORE_ID)
                .build();
        String requestJson = objectMapper.writeValueAsString(transactionDto);
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(NOT_FOUND.name()))
                .andExpect(jsonPath("$.messages").exists());
    }

    @Test
    void createWithMissingTransactionStoreId_shouldReturnBadRequest() throws Exception {
        TransactionCreateDto transactionDto = TransactionCreateDto.builder()
                .customerCard(13L)
                .transactionSum(79.97275934)
                .transactionDatetime(LocalDateTime.of(LocalDate.of(2021, SEPTEMBER, 15), LocalTime.of(15, 52, 53)))
                .build();
        String requestJson = objectMapper.writeValueAsString(transactionDto);
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ENTITY_IS_NOT_VALID.name()))
                .andExpect(jsonPath("$.messages").exists());
    }

    @Test
    void createWithMissingTransactionDatetime_shouldReturnBadRequest() throws Exception {
        TransactionCreateDto transactionDto = TransactionCreateDto.builder()
                .customerCard(13L)
                .transactionSum(79.97275934)
                .transactionStoreId(2L)
                .build();
        String requestJson = objectMapper.writeValueAsString(transactionDto);
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ENTITY_IS_NOT_VALID.name()))
                .andExpect(jsonPath("$.messages").exists());
    }

    @Test
    void createWithMissingTransactionSum_shouldReturnBadRequest() throws Exception {
        TransactionCreateDto transactionDto = TransactionCreateDto.builder()
                .customerCard(13L)
                .transactionDatetime(LocalDateTime.of(LocalDate.of(2021, SEPTEMBER, 15), LocalTime.of(15, 52, 53)))
                .transactionStoreId(2L)
                .build();
        String requestJson = objectMapper.writeValueAsString(transactionDto);
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ENTITY_IS_NOT_VALID.name()))
                .andExpect(jsonPath("$.messages").exists());
    }

    @Test
    void createWithNotExistingCustomerCard_shouldReturnBadRequest() throws Exception {
        TransactionCreateDto transactionDto = TransactionCreateDto.builder()
                .customerCard(NOT_EXISTING_CARD_ID)
                .transactionSum(79.97275934)
                .transactionDatetime(LocalDateTime.of(LocalDate.of(2021, SEPTEMBER, 15), LocalTime.of(15, 52, 53)))
                .transactionStoreId(2L)
                .build();
        String requestJson = objectMapper.writeValueAsString(transactionDto);
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(NOT_FOUND.name()))
                .andExpect(jsonPath("$.messages").exists());
    }

    @Test
    void deleteTransaction() throws Exception {
        mockMvc.perform(delete("/api/v1/cards/{TRANSACTION_ID}", TRANSACTION_ID))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/cards/{TRANSACTION_ID}", TRANSACTION_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(NOT_FOUND.name()))
                .andExpect(jsonPath("$.messages").exists());
    }

    private CardReadDto getCardReadDtoWithId(Long id) {
        return CardReadDto.builder().id(id).build();
    }
}