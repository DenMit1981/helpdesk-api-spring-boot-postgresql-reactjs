package com.training.denmit.helpdeskApi.dto.feedback;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedbackDto {

    private static final String INVALID_RATE = "Please, rate your satisfaction with the solution";

    private String user;

    @Pattern(regexp = "terrible|bad|medium|good|great", message = INVALID_RATE)
    @NotEmpty(message = INVALID_RATE)
    private String rate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    private String text;
}
