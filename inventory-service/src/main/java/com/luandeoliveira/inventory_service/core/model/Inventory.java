package com.luandeoliveira.inventory_service.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String productCode;

    @Column(nullable = false)
    private Integer available;
}