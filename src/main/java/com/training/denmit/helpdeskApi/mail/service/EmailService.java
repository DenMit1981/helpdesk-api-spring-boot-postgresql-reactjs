package com.training.denmit.helpdeskApi.mail.service;

public interface EmailService {

    void sendNewTicketMail(Long ticketId);

    void sendApprovedTicketMail(Long ticketId);

    void sendNewCancelledTicketMail(Long ticketId);

    void sendDeclinedTicketMail(Long ticketId);

    void sendApprovedCancelledTicketMail(Long ticketId);

    void sendDoneTicketMail(Long ticketId);

    void sendFeedbackMail(Long ticketId);
}
