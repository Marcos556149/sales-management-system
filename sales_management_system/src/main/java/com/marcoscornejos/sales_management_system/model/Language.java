/**
 * Represents the supported interface languages of the system.
 *
 * <p>This enum is used to define the available languages that can be selected
 * by the user for the application's interface.</p>
 *
 * <p>Each language includes a human-readable display name, which can be used
 * in the UI (e.g., dropdowns, labels).</p>
 */

package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum Language {

    EN("English"),
    ES("Spanish");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

}