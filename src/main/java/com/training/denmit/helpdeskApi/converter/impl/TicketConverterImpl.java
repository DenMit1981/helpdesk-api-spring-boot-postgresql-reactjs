package com.training.denmit.helpdeskApi.converter.impl;

import com.training.denmit.helpdeskApi.converter.TicketConverter;
import com.training.denmit.helpdeskApi.dto.ticket.TicketCreationDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketListViewDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketViewDto;
import com.training.denmit.helpdeskApi.model.Ticket;
import com.training.denmit.helpdeskApi.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TicketConverterImpl implements TicketConverter {

    @Override
    public Ticket fromTicketCreationDto(TicketCreationDto ticketDTO) {
        Ticket ticket = new Ticket();

        ticket.setName(ticketDTO.getName());
        ticket.setDescription(ticketDTO.getDescription());
        ticket.setDesiredResolutionDate(ticketDTO.getDesiredResolutionDate());
        ticket.setCategory(ticketDTO.getCategory());
        ticket.setUrgency(ticketDTO.getUrgency());

        return ticket;
    }

    @Override
    public TicketListViewDto convertToTicketListViewDto(Ticket ticket) {
        TicketListViewDto ticketListViewDto = new TicketListViewDto();

        ticketListViewDto.setId(ticket.getId());
        ticketListViewDto.setName(ticket.getName());
        ticketListViewDto.setDesiredResolutionDate(ticket.getDesiredResolutionDate());
        ticketListViewDto.setUrgency(ticket.getUrgency());
        ticketListViewDto.setStatus(ticket.getStatus());

        return ticketListViewDto;
    }

    @Override
    public TicketViewDto convertToTicketViewDto(Ticket ticket) {
        TicketViewDto ticketViewDto = new TicketViewDto();

        ticketViewDto.setId(ticket.getId());
        ticketViewDto.setName(ticket.getName());
        ticketViewDto.setCreatedOn(ticket.getCreatedOn());
        ticketViewDto.setStatus(ticket.getStatus());
        ticketViewDto.setUrgency(ticket.getUrgency());
        ticketViewDto.setDesiredResolutionDate(ticket.getDesiredResolutionDate());
        ticketViewDto.setTicketOwner(getUserFullName(ticket.getTicketOwner()));
        ticketViewDto.setApprover(getUserFullName(ticket.getApprover()));
        ticketViewDto.setAssignee(getUserFullName(ticket.getAssignee()));
        ticketViewDto.setDescription(ticket.getDescription());
        ticketViewDto.setCategory(ticket.getCategory());

        return ticketViewDto;
    }

    @Override
    public void editTicketFromTicketCreationDto(Ticket ticket, TicketCreationDto ticketDTO) {
        ticket.setName(ticketDTO.getName());
        ticket.setDescription(ticketDTO.getDescription());
        ticket.setDesiredResolutionDate(ticketDTO.getDesiredResolutionDate());
        ticket.setCategory(ticketDTO.getCategory());
        ticket.setUrgency(ticketDTO.getUrgency());
    }

    private String getUserFullName(User user) {
        if (user != null) {
            return user.getLastName() + " " + user.getFirstName();
        }
        return "Not assigned";
    }
}
