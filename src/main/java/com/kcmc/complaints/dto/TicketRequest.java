// File: TicketRequest.java
package com.kcmc.complaints.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketRequest {
    @NotBlank
    public String title;
    @NotBlank
    public String description;
    @NotNull
    public Long createdById;
    public Long assignedAgentId;  // optional
    @NotNull
    public Integer categoryId;
    @NotNull
    public Short priorityId;
    public String statusName;     // optional; default OPEN if null
}