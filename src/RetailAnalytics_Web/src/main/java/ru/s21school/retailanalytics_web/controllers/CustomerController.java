package ru.s21school.retailanalytics_web.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import ru.s21school.retailanalytics_web.dto.ErrorDto;
import ru.s21school.retailanalytics_web.dto.entityDto.customerDto.CustomerDto;
import ru.s21school.retailanalytics_web.dto.entityDto.customerDto.CustomerPageDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Optional;

@Controller
@RequestMapping("data/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {
    private static final String CUSTOMER_API_URL = "http://localhost:8081/api/v1/customers";

    private final RestTemplate restTemplate;

    @GetMapping
    public String getCustomersPage(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "30") int size,
                                   Model model) {
        ResponseEntity<CustomerPageDto> response =
                restTemplate.exchange(CUSTOMER_API_URL + String.format("?page=%d&size=%d", page, size),
                        HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                        });
        CustomerPageDto customersPage = Optional.ofNullable(response.getBody()).orElse(new CustomerPageDto());
        model.addAttribute("entities", customersPage.getContent());
        model.addAttribute("totalPages", customersPage.getTotalPages());
        model.addAttribute("totalElements", customersPage.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        return "customers/customers";
    }

    @GetMapping("new")
    public String getCreateCustomerPage(Model model) {
        model.addAttribute("customer", new CustomerDto());
        return "customers/new";
    }

    @PostMapping
    public String createCustomer(@Valid @ModelAttribute("customer") CustomerDto customer, Model model) {
        try {
            HttpEntity<CustomerDto> httpEntity = new HttpEntity<>(customer);
            ResponseEntity<LinkedHashMap<String, Object>> response =
                    restTemplate.exchange(CUSTOMER_API_URL,
                            HttpMethod.POST, httpEntity, new ParameterizedTypeReference<>() {
                            });
            LinkedHashMap<String, Object> map = response.getBody();
            log.info("New customer was saved. Id: {}", map.get("id"));
            return "redirect:/data/customers";
        } catch (HttpClientErrorException exception) {
            ErrorDto errorDto = exception.getResponseBodyAs(ErrorDto.class);
            log.warn(errorDto.getMessages().toString());
            model.addAttribute("errors", errorDto.getMessages());
            return "customers/new";
        }
    }


    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
        restTemplate.delete(CUSTOMER_API_URL + "/" + id);
        return "redirect:/data/customers";
    }

    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse servletResponse) throws IOException {
        Resource resource = restTemplate.getForObject(CUSTOMER_API_URL + "/export", Resource.class);
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"customers.tsv\"");
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.getWriter().print(resource.getContentAsString(StandardCharsets.UTF_8));
    }

    @PostMapping("/import")
    public String importFromCsv(@RequestParam MultipartFile file) {
        Resource invoicesResource = file.getResource();
        LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", invoicesResource);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<>(parts, httpHeaders);
        String url = CUSTOMER_API_URL + "/import";
        restTemplate.postForEntity(url, httpEntity, Object.class);
        return "redirect:/data/customers";
    }
}
