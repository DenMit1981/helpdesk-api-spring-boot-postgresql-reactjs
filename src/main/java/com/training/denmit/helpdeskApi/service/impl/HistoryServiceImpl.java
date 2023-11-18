package com.training.denmit.helpdeskApi.service.impl;

import com.training.denmit.helpdeskApi.converter.HistoryConverter;
import com.training.denmit.helpdeskApi.dto.history.HistoryDto;
import com.training.denmit.helpdeskApi.model.Attachment;
import com.training.denmit.helpdeskApi.model.History;
import com.training.denmit.helpdeskApi.model.Ticket;
import com.training.denmit.helpdeskApi.model.enums.Status;
import com.training.denmit.helpdeskApi.repository.HistoryRepository;
import com.training.denmit.helpdeskApi.service.HistoryService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private static final Logger LOGGER = LogManager.getLogger(HistoryServiceImpl.class.getName());

    private static final String ACTION_CREATE_TICKET = "Ticket was created";
    private static final String DESCRIPTION_CREATE_TICKET = "Ticket № %s was created";
    private static final String ACTION_UPDATE_TICKET = "Ticket was updated";
    private static final String DESCRIPTION_UPDATE_TICKET = "Ticket № %s was updated";
    private static final String ACTION_CHANGE_TICKET_STATUS = "Ticket status is changed";
    private static final String DESCRIPTION_CHANGE_TICKET_STATUS = "Ticket status is changed from %s to %s";
    private static final String ACTION_ATTACH_FILE_TO_TICKET = "File was attached";
    private static final String DESCRIPTION_ATTACH_FILE_TO_TICKET = "File %s was attached to ticket %s";
    private static final String ACTION_REMOVE_FILE_FROM_TICKET = "File was removed";
    private static final String DESCRIPTION_REMOVE_FILE_FROM_TICKET = "File %s was removed from ticket %s";

    private final HistoryRepository historyRepository;
    private final HistoryConverter historyConverter;

    @Override
    @Transactional
    public List<HistoryDto> getAllByTicketId(Long ticketId, String buttonValue) {
        List<History> history;

        if (buttonValue.equals("Show All")) {
            history = historyRepository.findAllByTicketIdOrderByDate(ticketId);

            LOGGER.info("All history for ticket {}: {}", ticketId, history);
        } else {
            history = historyRepository.findAllByTicketId(ticketId, PageRequest.of(0, 5,
                    Sort.by(Sort.Direction.DESC, "date")));

            LOGGER.info("Last 5 history for ticket {}: {}", ticketId, history);
        }

        return history.stream()
                .map(historyConverter::convertToHistoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveHistoryForCreatedTicket(Ticket ticket) {
        saveHistoryParameters(ACTION_CREATE_TICKET, String.format(DESCRIPTION_CREATE_TICKET, ticket.getId()), ticket);
    }

    @Override
    @Transactional
    public void saveHistoryForUpdatedTicket(Ticket ticket) {
        saveHistoryParameters(ACTION_UPDATE_TICKET, String.format(DESCRIPTION_UPDATE_TICKET, ticket.getId()), ticket);
    }

    @Override
    @Transactional
    public void saveHistoryForChangedTicketStatus(Ticket ticket, Status previousStatus, Status newStatus) {
        saveHistoryParameters(ACTION_CHANGE_TICKET_STATUS, String.format(DESCRIPTION_CHANGE_TICKET_STATUS,
                previousStatus, newStatus), ticket);
    }

    @Override
    @Transactional
    public void saveHistoryForAttachedFile(Attachment attachment, Ticket ticket) {
        saveHistoryParameters(ACTION_ATTACH_FILE_TO_TICKET, String.format(DESCRIPTION_ATTACH_FILE_TO_TICKET,
                attachment.getName(), ticket.getId()), ticket);
    }

    @Override
    @Transactional
    public void saveHistoryForRemovedFile(String fileName, Ticket ticket) {
        saveHistoryParameters(ACTION_REMOVE_FILE_FROM_TICKET, String.format(DESCRIPTION_REMOVE_FILE_FROM_TICKET,
                fileName, ticket.getId()), ticket);
    }

    private void saveHistoryParameters(String action, String description, Ticket ticket) {
        History history = new History();

        history.setDate(LocalDateTime.now());
        history.setTicket(ticket);
        history.setUser(ticket.getTicketOwner());
        history.setAction(action);
        history.setDescription(description);

        historyRepository.save(history);
    }
}
