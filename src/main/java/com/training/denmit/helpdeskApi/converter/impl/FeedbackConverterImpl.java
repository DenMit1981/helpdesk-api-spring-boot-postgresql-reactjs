package com.training.denmit.helpdeskApi.converter.impl;

import com.training.denmit.helpdeskApi.converter.FeedbackConverter;
import com.training.denmit.helpdeskApi.dto.feedback.FeedbackDto;
import com.training.denmit.helpdeskApi.model.Feedback;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FeedbackConverterImpl implements FeedbackConverter {

    @Override
    public FeedbackDto convertToFeedbackDto(Feedback feedback) {
        FeedbackDto feedbackDto = new FeedbackDto();

        feedbackDto.setUser(feedback.getUser().getLastName() + " " + feedback.getUser().getFirstName());
        feedbackDto.setRate(feedback.getRate());
        feedbackDto.setDate(feedback.getDate());
        feedbackDto.setText(feedback.getText());

        return feedbackDto;
    }

    @Override
    public Feedback fromFeedbackDto(FeedbackDto feedbackDto) {
        Feedback feedback = new Feedback();

        feedback.setRate(feedbackDto.getRate());
        feedback.setText(feedbackDto.getText());

        return feedback;
    }
}
