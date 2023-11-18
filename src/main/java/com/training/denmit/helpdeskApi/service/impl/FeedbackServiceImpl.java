package com.training.denmit.helpdeskApi.service.impl;

import com.training.denmit.helpdeskApi.converter.FeedbackConverter;
import com.training.denmit.helpdeskApi.dto.feedback.FeedbackDto;
import com.training.denmit.helpdeskApi.mail.service.EmailService;
import com.training.denmit.helpdeskApi.model.Feedback;
import com.training.denmit.helpdeskApi.model.Ticket;
import com.training.denmit.helpdeskApi.repository.FeedbackRepository;
import com.training.denmit.helpdeskApi.service.FeedbackService;
import com.training.denmit.helpdeskApi.service.TicketService;
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
public class FeedbackServiceImpl implements FeedbackService {

    private static final Logger LOGGER = LogManager.getLogger(FeedbackServiceImpl.class.getName());

    private final FeedbackRepository feedbackRepository;
    private final TicketService ticketService;
    private final FeedbackConverter feedbackConverter;
    private final EmailService emailService;

    @Override
    @Transactional
    public Feedback save(FeedbackDto feedbackDto, Long ticketId, String login) {
        ticketService.checkAccessToFeedbackTicket(login, ticketId);

        Ticket ticket = ticketService.findById(ticketId);

        Feedback feedback = feedbackConverter.fromFeedbackDto(feedbackDto);

        feedback.setTicket(ticket);
        feedback.setUser(ticket.getTicketOwner());
        feedback.setDate(LocalDateTime.now());

        feedbackRepository.save(feedback);

        emailService.sendFeedbackMail(ticketId);

        LOGGER.info("New feedback has just been added to ticket {}: rate - {}", ticketId, feedback.getRate());

        return feedback;
    }

    @Override
    @Transactional
    public List<FeedbackDto> getAllByTicketId(Long ticketId, String buttonValue) {
        List<Feedback> feedbacks;

        if (buttonValue.equals("Show All")) {
            feedbacks = feedbackRepository.findAllByTicketId(ticketId);

            LOGGER.info("All feedbacks for ticket {}: {}", ticketId, feedbacks);
        } else {
            feedbacks = feedbackRepository.findAllByTicketId(ticketId, PageRequest.of(0, 5,
                    Sort.by(Sort.Direction.DESC, "date")));

            LOGGER.info("Last 5 feedbacks for ticket {}: {}", ticketId, feedbacks);
        }

        return feedbacks.stream()
                .map(feedbackConverter::convertToFeedbackDto)
                .collect(Collectors.toList());
    }
}
