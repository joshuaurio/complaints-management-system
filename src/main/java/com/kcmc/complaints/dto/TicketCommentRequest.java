// TicketCommentRequest.java
package com.kcmc.complaints.dto;

public class TicketCommentRequest {
    public Long ticketId;
    public Long authorId;
    public String content;
    public Boolean isInternal;
}
