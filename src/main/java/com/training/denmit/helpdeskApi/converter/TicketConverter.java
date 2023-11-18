package com.training.denmit.helpdeskApi.converter;

import com.training.denmit.helpdeskApi.dto.ticket.TicketCreationDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketListViewDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketViewDto;
import com.training.denmit.helpdeskApi.model.Ticket;

public interface TicketConverter {

    Ticket fromTicketCreationDto(TicketCreationDto ticketDTO);

    TicketListViewDto convertToTicketListViewDto(Ticket ticket);

    TicketViewDto convertToTicketViewDto(Ticket ticket);

    void editTicketFromTicketCreationDto(Ticket ticket, TicketCreationDto ticketDTO);
}
