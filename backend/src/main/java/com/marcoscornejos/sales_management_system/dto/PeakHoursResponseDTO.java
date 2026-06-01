package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO that represents peak sales hour statistics
 * calculated based on the selected filters.
 *
 * <p>
 * This DTO contains:
 * <ul>
 *   <li>The hour with the highest revenue generated</li>
 *   <li>The hour with the highest number of sales</li>
 * </ul>
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeakHoursResponseDTO {

    /**
     * Hour with the highest revenue generated.
     *
     * <p>
     * Represents the hour interval that accumulated
     * the greatest total revenue within the selected filters.
     * </p>
     *
     * <p>
     * May be {@code null} if no matching sales exist.
     * </p>
     */
    private String highestRevenueHour;

    /**
     * Hour with the highest number of sales.
     *
     * <p>
     * Represents the hour interval with the greatest
     * number of sales within the selected filters.
     * </p>
     *
     * <p>
     * May be {@code null} if no matching sales exist.
     * </p>
     */
    private String highestSalesHour;
}
