package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO that represents a user available for filtering
 * in the statistics module.
 *
 * <p>
 * This DTO is used by the frontend to display selectable users
 * in filter dropdowns.
 * </p>
 *
 * <p>
 * The frontend uses:
 * <ul>
 *   <li>userId → for backend filtering</li>
 *   <li>username → for display purposes</li>
 * </ul>
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterDTO {

    /**
     * Unique identifier of the user.
     *
     * <p>
     * This value is sent to the backend when filtering statistics.
     * </p>
     */
    private Long userId;

    /**
     * Display name of the user.
     *
     * <p>
     * This value is shown in the frontend UI.
     * </p>
     */
    private String userName;
}