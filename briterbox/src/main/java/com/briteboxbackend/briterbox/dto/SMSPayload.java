package com.briteboxbackend.briterbox.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SMSPayload {
    private String phoneNumber;
    private String message;

    public String toXml() {
        // Convert SMSPayload to XML format
        // Example implementation:
        return "<SMSPayload><phoneNumber>" + phoneNumber + "</phoneNumber><message>" + message + "</message></SMSPayload>";
    }

}
