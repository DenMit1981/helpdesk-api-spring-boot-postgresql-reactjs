package com.training.denmit.helpdeskApi.converter;

import com.training.denmit.helpdeskApi.dto.feedback.FeedbackDto;
import com.training.denmit.helpdeskApi.model.Feedback;

public interface FeedbackConverter {

    FeedbackDto convertToFeedbackDto(Feedback feedback);

    Feedback fromFeedbackDto(FeedbackDto feedbackDto);
}
