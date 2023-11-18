package com.training.denmit.helpdeskApi.service;

import com.training.denmit.helpdeskApi.dto.history.HistoryDto;
import com.training.denmit.helpdeskApi.model.Attachment;
import com.training.denmit.helpdeskApi.model.Ticket;
import com.training.denmit.helpdeskApi.model.enums.Status;

import java.util.List;

public interface HistoryService {

    List<HistoryDto> getAllByTicketId(Long ticketId, String buttonValue);

    void saveHistoryForCreatedTicket(Ticket ticket);

    void saveHistoryForUpdatedTicket(Ticket ticket);

    void saveHistoryForChangedTicketStatus(Ticket ticket, Status previousStatus, Status newStatus);

    void saveHistoryForAttachedFile(Attachment attachment, Ticket ticket);

    void saveHistoryForRemovedFile(String fileName, Ticket ticket);
}
