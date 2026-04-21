package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Generic DTO to represent enum values with a code and a label.
 */
@Getter @Setter
@AllArgsConstructor
public class EnumDTO {

    /** Internal code of the enum (e.g., ADMIN, ACTIVE). */
    private String code;

    /** Human-readable label (e.g., Administrator, Active). */
    private String label;
}