package com.training.denmit.helpdeskApi.controller;

import com.training.denmit.helpdeskApi.dto.comment.CommentDto;
import com.training.denmit.helpdeskApi.model.Comment;
import com.training.denmit.helpdeskApi.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/tickets/{ticketId}/comments")
@Api("Comment controller")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ApiOperation(value = "Create a new comment")
    public ResponseEntity<?> save(@Valid @RequestBody CommentDto commentDto,
                                  @PathVariable("ticketId") Long ticketId) {
        Comment savedComment = commentService.save(commentDto, ticketId);

        String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        String savedCommentLocation = currentUri + "/" + savedComment.getId();

        return ResponseEntity.status(CREATED)
                .header(HttpHeaders.LOCATION, savedCommentLocation)
                .body(savedComment);
    }

    @GetMapping
    @ApiOperation(value = "Get all comments by ticket ID")
    public ResponseEntity<List<CommentDto>> getAllByTicketId(@PathVariable("ticketId") Long ticketId,
                                                             @RequestParam(value = "buttonValue", defaultValue = "default") String buttonValue) {
        return ResponseEntity.ok(commentService.getAllByTicketId(ticketId, buttonValue));
    }
}
