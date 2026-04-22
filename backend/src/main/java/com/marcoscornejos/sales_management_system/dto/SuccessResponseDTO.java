package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic response DTO for successful operations.
 *
 * <p>
 * Used to standardize all success responses across the API,
 * providing a consistent structure for frontend consumption.
 * </p>
 *
 * @param <T> type of data returned in the response
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponseDTO<T> {

    /**
     * Machine-readable success code used by the frontend
     * to determine the type of operation executed.
     */
    private String code;

    /**
     * Human-readable message describing the result of the operation.
     */
    private String message;

    /**
     * Optional payload returned by the operation.
     */
    private T data;
}