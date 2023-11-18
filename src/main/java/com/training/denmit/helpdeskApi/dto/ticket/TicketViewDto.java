package com.training.denmit.helpdeskApi.dto.ticket;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.training.denmit.helpdeskApi.model.enums.Category;
import com.training.denmit.helpdeskApi.model.enums.Status;
import com.training.denmit.helpdeskApi.model.enums.Urgency;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TicketViewDto {

    private Long id;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdOn;

    private Status status;

    private Urgency urgency;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate desiredResolutionDate;

    private String ticketOwner;

    private String approver;

    private String assignee;

    private String description;

    private Category category;
}
