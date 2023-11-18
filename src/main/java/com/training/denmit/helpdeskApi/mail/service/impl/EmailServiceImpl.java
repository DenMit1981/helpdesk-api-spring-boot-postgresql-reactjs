package com.training.denmit.helpdeskApi.mail.service.impl;

import com.training.denmit.helpdeskApi.mail.service.EmailService;
import com.training.denmit.helpdeskApi.model.User;
import com.training.denmit.helpdeskApi.repository.TicketRepository;
import com.training.denmit.helpdeskApi.repository.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${mail.username}")
    private String sendFrom;

    @Value("${spring.permitted.url}")
    private String baseUrl;

    private static final String TICKET_ID = "ticketId";
    private static final String BASE_URL = "baseUrl";
    private static final String USER_NAME = "username";
    private static final String USER_LASTNAME = "userSurname";

    private final SpringTemplateEngine thymeleafTemplateEngine;
    private final JavaMailSender emailSender;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    public EmailServiceImpl(SpringTemplateEngine thymeleafTemplateEngine, JavaMailSender emailSender,
                            UserRepository userRepository, TicketRepository ticketRepository) {
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
        this.emailSender = emailSender;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void sendNewTicketMail(Long ticketId) {
        List<User> recipients = userRepository.findAllManagers();

        sendMessageToSeveralRecipients(recipients, ticketId, "New ticket for approval", "template#1-new.html");
    }

    @Override
    public void sendApprovedTicketMail(Long ticketId) {
        List<User> recipients = userRepository.findAllEngineers();

        recipients.add(ticketRepository.findById(ticketId).get().getTicketOwner());

        sendMessageToSeveralRecipients(recipients, ticketId, "Ticket was approved", "template#2-approved.html");
    }

    @Override
    public void sendDeclinedTicketMail(Long ticketId) {
        User recipient = ticketRepository.findById(ticketId).get().getTicketOwner();

        sendMessageToIndividualRecipient(recipient, ticketId, "Ticket was declined", "template#3-declined.html");
    }

    @Override
    public void sendNewCancelledTicketMail(Long ticketId) {
        User recipient = ticketRepository.findById(ticketId).get().getTicketOwner();

        sendMessageToIndividualRecipient(recipient, ticketId, "Ticket was canceled", "template#4-new-cancelled.html");
    }

    @Override
    public void sendApprovedCancelledTicketMail(Long ticketId) {
        List<User> recipients = new ArrayList<>();

        recipients.add(ticketRepository.findById(ticketId).get().getTicketOwner());
        recipients.add(ticketRepository.findById(ticketId).get().getApprover());

        sendMessageToSeveralRecipients(recipients, ticketId, "Ticket was canceled", "template#5-approved-cancelled.html");
    }

    @Override
    public void sendDoneTicketMail(Long ticketId) {
        User recipient = ticketRepository.findById(ticketId).get().getTicketOwner();

        sendMessageToIndividualRecipient(recipient, ticketId, "Ticket was done", "template#6-done.html");
    }

    @Override
    public void sendFeedbackMail(Long ticketId) {
        User recipient = ticketRepository.findById(ticketId).get().getAssignee();

        sendMessageToIndividualRecipient(recipient, ticketId, "Feedback was provided", "template#7-feedback.html");
    }

    private void sendMessageToIndividualRecipient(User recipient, Long ticketId, String subject, String template) {
        sendMessageUsingThymeleafTemplate(recipient.getEmail(), subject,
                Map.of(
                        TICKET_ID, ticketId,
                        USER_NAME, recipient.getFirstName(),
                        USER_LASTNAME, recipient.getLastName(),
                        BASE_URL, baseUrl
                ), template);
    }

    private void sendMessageToSeveralRecipients(List<User> recipients, Long ticketId, String subject, String template) {
        recipients.forEach(recipient -> sendMessageUsingThymeleafTemplate(recipient.getEmail(), subject,
                Map.of(
                        TICKET_ID, ticketId,
                        BASE_URL, baseUrl
                ), template));
    }

    private void sendMessageUsingThymeleafTemplate(String to, String subject, Map<String, Object> templateModel,
                                                   String template) {
        Context thymeleafContext = new Context();

        thymeleafContext.setVariables(templateModel);

        String htmlBody = thymeleafTemplateEngine.process(template, thymeleafContext);

        sendHtmlMessage(to, subject, htmlBody);
    }

    @SneakyThrows
    private void sendHtmlMessage(String to, String subject, String htmlBody) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(sendFrom);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        emailSender.send(message);
    }
}

