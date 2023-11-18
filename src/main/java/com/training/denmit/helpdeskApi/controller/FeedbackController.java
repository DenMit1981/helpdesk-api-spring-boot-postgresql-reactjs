package com.training.denmit.helpdeskApi.controller;

import com.training.denmit.helpdeskApi.dto.feedback.FeedbackDto;
import com.training.denmit.helpdeskApi.model.Feedback;
import com.training.denmit.helpdeskApi.service.FeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/tickets/{ticketId}/feedbacks")
@Api("Feedback controller")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @ApiOperation(value = "Create a new feedback", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> save(@Valid @RequestBody FeedbackDto feedbackDto,
                                  @PathVariable("ticketId") Long ticketId,
                                  Principal principal) {
        Feedback savedFeedback = feedbackService.save(feedbackDto, ticketId, principal.getName());

        String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        String savedFeedbackLocation = currentUri + "/" + savedFeedback.getId();

        return ResponseEntity.status(CREATED)
                .header(HttpHeaders.LOCATION, savedFeedbackLocation)
                .body(savedFeedback);
    }

    @GetMapping
    @ApiOperation(value = "Get all feedbacks by ticket ID")
    public ResponseEntity<List<FeedbackDto>> getAllByTicketId(@PathVariable("ticketId") Long ticketId,
                                                              @RequestParam(value = "buttonValue", defaultValue = "default") String buttonValue) {
        return ResponseEntity.ok(feedbackService.getAllByTicketId(ticketId, buttonValue));
    }
}
