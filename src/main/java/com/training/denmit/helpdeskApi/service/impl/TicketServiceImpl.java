package com.training.denmit.helpdeskApi.service.impl;

import com.training.denmit.helpdeskApi.converter.TicketConverter;
import com.training.denmit.helpdeskApi.dto.ticket.TicketCreationDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketListViewDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketViewDto;
import com.training.denmit.helpdeskApi.exception.AccessDeniedException;
import com.training.denmit.helpdeskApi.exception.StatusNotFoundException;
import com.training.denmit.helpdeskApi.exception.TicketNotFoundException;
import com.training.denmit.helpdeskApi.mail.service.EmailGenerateService;
import com.training.denmit.helpdeskApi.mail.service.EmailService;
import com.training.denmit.helpdeskApi.model.Ticket;
import com.training.denmit.helpdeskApi.model.User;
import com.training.denmit.helpdeskApi.model.enums.Role;
import com.training.denmit.helpdeskApi.model.enums.Status;
import com.training.denmit.helpdeskApi.repository.TicketRepository;
import com.training.denmit.helpdeskApi.service.HistoryService;
import com.training.denmit.helpdeskApi.service.TicketService;
import com.training.denmit.helpdeskApi.service.UserService;
import com.training.denmit.helpdeskApi.util.comparator.StatusComparator;
import com.training.denmit.helpdeskApi.util.comparator.UrgencyComparator;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.training.denmit.helpdeskApi.model.enums.Role.*;
import static com.training.denmit.helpdeskApi.model.enums.Status.*;

@Service
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {

    private static final Logger LOGGER = LogManager.getLogger(TicketServiceImpl.class.getName());

    private static final String ACCESS_DENIED_FOR_CREATING = "You can't have access to create ticket";
    private static final String ACCESS_DENIED_FOR_CHANGING_STATUS = "You can't change status of current ticket";
    private static final String TICKET_ALREADY_HAS_THIS_STATUS = "Ticket already has this status";
    private static final String ACCESS_DENIED_FOR_FORMATTING_OWN_TICKET = "You can't formatted your own ticket";
    private static final String TICKET_NOT_FOUND_BY_ID = "Ticket with id %s not found";

    private static final Map<String, Comparator<Ticket>> SORT_MAP = Map.of(
            "id", Comparator.comparing(Ticket::getId),
            "name", Comparator.comparing(Ticket::getName),
            "desiredResolutionDate", Comparator.comparing(Ticket::getDesiredResolutionDate),
            "urgency", Comparator.comparing(Ticket::getUrgency, new UrgencyComparator()),
            "status", Comparator.comparing(Ticket::getStatus, new StatusComparator()),
            "default", Comparator.comparing(Ticket::getUrgency, new UrgencyComparator())
                    .thenComparing(Comparator.comparing(Ticket::getDesiredResolutionDate).reversed())
    );

    private static final Map<Role, Set<Status>> ACCESS_TO_CHANGE_STATUS = Map.of(
            ROLE_EMPLOYEE, Set.of(NEW, CANCELED),
            ROLE_MANAGER, Set.of(NEW, CANCELED, APPROVED, DECLINED),
            ROLE_ENGINEER, Set.of(IN_PROGRESS, DONE, CANCELED)
    );

    private final TicketRepository ticketRepository;
    private final TicketConverter ticketConverter;
    private final UserService userService;
    private final EmailService emailService;
    private final EmailGenerateService emailGenerateService;
    private final HistoryService historyService;

    @Override
    @Transactional
    public Ticket save(TicketCreationDto ticketCreationDto, String login, String buttonValue) {
        checkAccessToCreateTicket(login);

        Ticket ticket = ticketConverter.fromTicketCreationDto(ticketCreationDto);

        User user = userService.getByLogin(login);

        ticket.setTicketOwner(user);
        ticket.setCreatedOn(LocalDate.now());

        buttonClickEventsWithTicket(ticket, getNewTicketId(), buttonValue);

        ticketRepository.save(ticket);

        historyService.saveHistoryForCreatedTicket(ticket);

        LOGGER.info("New ticket : {}", ticket);

        return ticket;
    }

    @Override
    @Transactional
    public TicketViewDto getById(Long id) {
        return ticketConverter.convertToTicketViewDto(findById(id));
    }

    @Override
    @Transactional
    public List<TicketListViewDto> getAll(String login, String parameter, String sortField,
                                          String sortDirection, int pageSize, int pageNumber) {
        User user = userService.getByLogin(login);

        List<Ticket> tickets = new ArrayList<>();

        if (user.getRole().equals(Role.ROLE_EMPLOYEE)) {
            tickets = getSortedTickets(ticketRepository.findAllForEmployeeByParameter(user.getId(),
                    parameter, PageRequest.of(pageNumber - 1, pageSize)), sortField, sortDirection);
        }
        if (user.getRole().equals(Role.ROLE_MANAGER)) {
            tickets = getSortedTickets(ticketRepository.findAllForManagerByParameter(user.getId(),
                    parameter, PageRequest.of(pageNumber - 1, pageSize)), sortField, sortDirection);
        }
        if (user.getRole().equals(Role.ROLE_ENGINEER)) {
            tickets = getSortedTickets(ticketRepository.findAllForEngineerByParameter(user.getId(),
                    parameter, PageRequest.of(pageNumber - 1, pageSize)), sortField, sortDirection);
        }

        LOGGER.info("All tickets: {}", tickets);

        return tickets.stream()
                .map(ticketConverter::convertToTicketListViewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketListViewDto> getOwn(String login, String parameter, String sortField,
                                          String sortDirection, int pageSize, int pageNumber) {
        User user = userService.getByLogin(login);

        List<Ticket> tickets = new ArrayList<>();

        if (user.getRole().equals(Role.ROLE_EMPLOYEE)) {
            tickets = getSortedTickets(ticketRepository.findAllForEmployeeByParameter(user.getId(),
                    parameter, PageRequest.of(pageNumber - 1, pageSize)), sortField, sortDirection);
        }
        if (user.getRole().equals(Role.ROLE_MANAGER)) {
            tickets = getSortedTickets(ticketRepository.findOwnForManagerByParameter(user.getId(),
                    parameter, PageRequest.of(pageNumber - 1, pageSize)), sortField, sortDirection);
        }
        if (user.getRole().equals(Role.ROLE_ENGINEER)) {
            tickets = getSortedTickets(ticketRepository.findOwnForEngineerByParameter(user.getId(),
                    parameter, PageRequest.of(pageNumber - 1, pageSize)), sortField, sortDirection);
        }

        LOGGER.info("My tickets: {}", tickets);

        return tickets.stream()
                .map(ticketConverter::convertToTicketListViewDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Ticket update(Long ticketId, TicketCreationDto ticketDto, String login, String buttonValue) {
        Ticket updatedTicket = findById(ticketId);

        ticketConverter.editTicketFromTicketCreationDto(updatedTicket, ticketDto);

        buttonClickEventsWithTicket(updatedTicket, ticketId, buttonValue);

        ticketRepository.save(updatedTicket);

        historyService.saveHistoryForUpdatedTicket(updatedTicket);

        LOGGER.info("Updated ticket : {}", updatedTicket);

        return updatedTicket;
    }

    @Override
    @Transactional
    public void changeTicketStatus(String login, Long ticketId, String status) {
        checkAccessToChangeTicketStatus(login, ticketId, status);

        Ticket ticket = findById(ticketId);

        User user = userService.getByLogin(login);

        Status previousStatus = ticket.getStatus();
        Status newStatus = Status.valueOf(status.toUpperCase());

        if (newStatus != previousStatus) {
            changeStatusEventsWithTicket(ticket, user, newStatus);

            ticketRepository.save(ticket);

            LOGGER.info("Status for ticket {} was changed from {} to {}", ticketId, previousStatus, newStatus);

            historyService.saveHistoryForChangedTicketStatus(ticket, previousStatus, newStatus);
        } else {
            LOGGER.error(TICKET_ALREADY_HAS_THIS_STATUS);

            throw new AccessDeniedException(TICKET_ALREADY_HAS_THIS_STATUS);
        }
    }

    @Override
    @Transactional
    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(String.format(TICKET_NOT_FOUND_BY_ID, id)));
    }

    @Override
    @Transactional
    public void checkAccessToFeedbackTicket(String login, Long ticketId) {
        User user = userService.getByLogin(login);

        ticketRepository.checkAccessToFeedbackTicket(user.getId(), ticketId)
                .orElseThrow(() -> new AccessDeniedException("You don't have access to provide feedback for this ticket"));
    }

    @Override
    public Long getNewTicketId() {
        List<Ticket> tickets = (List<Ticket>) ticketRepository.findAll();

        return tickets.size() + 1L;
    }

    private List<Ticket> getSortedTickets(List<Ticket> tickets, String sortField, String sortDirection) {
        if (!sortDirection.equals("desc")) {
            tickets.sort(SORT_MAP.get(sortField));
        } else {
            tickets.sort(SORT_MAP.get(sortField).reversed());
        }

        return tickets;
    }

    private void buttonClickEventsWithTicket(Ticket ticket, Long ticketId, String buttonValue) {
        if (buttonValue.equals("SaveAsDraft")) {
            ticket.setStatus(Status.DRAFT);
        } else {
            ticket.setStatus(Status.NEW);

            emailService.sendNewTicketMail(ticketId);
        }
    }

    private void changeStatusEventsWithTicket(Ticket ticket, User user, Status newStatus) {
        if (newStatus == Status.APPROVED) {
            emailGenerateService.generateEmail(ticket.getId(), ticket.getStatus(), newStatus);

            ticket.setStatus(newStatus);
            ticket.setApprover(user);
        } else if (newStatus == Status.IN_PROGRESS) {
            emailGenerateService.generateEmail(ticket.getId(), ticket.getStatus(), newStatus);

            ticket.setStatus(newStatus);
            ticket.setAssignee(user);
        } else {
            emailGenerateService.generateEmail(ticket.getId(), ticket.getStatus(), newStatus);

            ticket.setStatus(newStatus);
        }
    }

    private void checkAccessToCreateTicket(String login) {
        if (userService.getByLogin(login).getRole().equals(Role.ROLE_ENGINEER)) {
            LOGGER.error(ACCESS_DENIED_FOR_CREATING);

            throw new AccessDeniedException(ACCESS_DENIED_FOR_CREATING);
        }
    }

    private void checkAccessToChangeTicketStatus(String login, Long ticketId, String newStatus) {
        User user = userService.getByLogin(login);

        Ticket ticket = findById(ticketId);

        if (ticket.getTicketOwner().getEmail().equals(user.getEmail()) && ticket.getStatus() != DRAFT) {
            LOGGER.error(ACCESS_DENIED_FOR_FORMATTING_OWN_TICKET);

            throw new AccessDeniedException(ACCESS_DENIED_FOR_FORMATTING_OWN_TICKET);
        }

        Status status = getStatusFromString(newStatus);

        boolean thereIsAccess = ACCESS_TO_CHANGE_STATUS.get(user.getRole()).stream()
                .anyMatch(states -> states == status);

        if (!thereIsAccess) {
            LOGGER.error(ACCESS_DENIED_FOR_CHANGING_STATUS);

            throw new AccessDeniedException(ACCESS_DENIED_FOR_CHANGING_STATUS);
        }
    }

    private Status getStatusFromString(String newStatus) {
        try {
            if (newStatus.contains("-")) {
                newStatus = newStatus.replace("-", "_");
            }
            return Status.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new StatusNotFoundException(String.format("This status \"%s\" does not exist", newStatus));
        }
    }
}

