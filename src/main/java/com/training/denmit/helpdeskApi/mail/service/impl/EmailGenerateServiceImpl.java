package com.training.denmit.helpdeskApi.mail.service.impl;

import com.training.denmit.helpdeskApi.mail.enums.ChangeStatusMail;
import com.training.denmit.helpdeskApi.mail.service.EmailGenerateService;
import com.training.denmit.helpdeskApi.mail.service.EmailService;
import com.training.denmit.helpdeskApi.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class EmailGenerateServiceImpl implements EmailGenerateService {

    private final Map<ChangeStatusMail, Consumer<Long>> sendStatus;

    @Autowired
    public EmailGenerateServiceImpl(EmailService emailService) {
        sendStatus = Map.of(
                ChangeStatusMail.DRAFT_NEW, emailService::sendNewTicketMail,
                ChangeStatusMail.DECLINED_NEW, emailService::sendNewTicketMail,
                ChangeStatusMail.NEW_APPROVED, emailService::sendApprovedTicketMail,
                ChangeStatusMail.NEW_CANCELED, emailService::sendNewCancelledTicketMail,
                ChangeStatusMail.NEW_DECLINED, emailService::sendDeclinedTicketMail,
                ChangeStatusMail.APPROVED_CANCELED, emailService::sendApprovedCancelledTicketMail,
                ChangeStatusMail.IN_PROGRESS_DONE, emailService::sendDoneTicketMail
        );
    }

    @Override
    public void generateEmail(Long ticketId, Status previousStatus, Status newStatus) {
        Map<Status, Status> ticketStatus = Map.of(previousStatus, newStatus);

        Arrays.stream(ChangeStatusMail.values())
                .filter(states -> states.isPreviousStatusToCurrentStatusMapEqualTo(ticketStatus))
                .findFirst()
                .ifPresent(status -> sendStatus.get(status).accept(ticketId));
    }
}
