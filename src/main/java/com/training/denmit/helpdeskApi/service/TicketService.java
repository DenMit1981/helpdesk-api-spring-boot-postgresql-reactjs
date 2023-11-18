package com.training.denmit.helpdeskApi.service;

import com.training.denmit.helpdeskApi.dto.ticket.TicketCreationDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketListViewDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketViewDto;
import com.training.denmit.helpdeskApi.model.Ticket;

import java.util.List;

public interface TicketService {

    Ticket save(TicketCreationDto ticketCreationDto, String login, String buttonValue);

    TicketViewDto getById(Long id);

    List<TicketListViewDto> getAll(String login, String parameter, String sortField,
                                   String sortDirection, int pageSize, int pageNumber);

    List<TicketListViewDto> getOwn(String login, String parameter, String sortField,
                                   String sortDirection, int pageSize, int pageNumber);

    Ticket update(Long ticketId, TicketCreationDto ticketEditDto, String login, String buttonValue);

    void changeTicketStatus(String login, Long ticketId, String status);

    Ticket findById(Long id);

    void checkAccessToFeedbackTicket(String login, Long ticketId);

    Long getNewTicketId();
}
