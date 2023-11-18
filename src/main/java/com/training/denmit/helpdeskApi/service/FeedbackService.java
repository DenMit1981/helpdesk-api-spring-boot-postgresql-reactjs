package com.training.denmit.helpdeskApi.service;

import com.training.denmit.helpdeskApi.model.Feedback;
import com.training.denmit.helpdeskApi.dto.feedback.FeedbackDto;;

import java.util.List;

public interface FeedbackService {

    Feedback save(FeedbackDto feedbackDto, Long ticketId, String login);

    List<FeedbackDto> getAllByTicketId(Long ticketId, String buttonValue);
}
