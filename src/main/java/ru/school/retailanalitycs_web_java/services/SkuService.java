package ru.school.retailanalitycs_web_java.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.school.retailanalitycs_web_java.entities.tables.Sku;

@Service
public class SkuService extends BaseService<Sku, Integer> {
    @Autowired
    protected SkuService(JpaRepository<Sku, Integer> repository) {
        super(repository);
    }
}