package ru.school.retailanalitycs_web_java.entities.tables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CheckId implements Serializable {
    @Serial
    private static final long serialVersionUID = -5623599323863520535L;

    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @Column(name = "sku_id", nullable = false)
    private Long skuId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CheckId entity = (CheckId) o;
        return Objects.equals(this.transactionId, entity.transactionId) &&
                Objects.equals(this.skuId, entity.skuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, skuId);
    }

}