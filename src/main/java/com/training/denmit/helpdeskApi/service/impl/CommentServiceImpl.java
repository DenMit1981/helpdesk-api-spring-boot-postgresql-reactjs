package com.training.denmit.helpdeskApi.service.impl;

import com.training.denmit.helpdeskApi.converter.CommentConverter;
import com.training.denmit.helpdeskApi.dto.comment.CommentDto;
import com.training.denmit.helpdeskApi.model.Comment;
import com.training.denmit.helpdeskApi.model.Ticket;
import com.training.denmit.helpdeskApi.repository.CommentRepository;
import com.training.denmit.helpdeskApi.service.CommentService;
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
public class CommentServiceImpl implements CommentService {

    private static final Logger LOGGER = LogManager.getLogger(CommentServiceImpl.class.getName());

    private final CommentRepository commentRepository;
    private final CommentConverter commentConverter;
    private final TicketService ticketService;

    @Override
    @Transactional
    public Comment save(CommentDto commentDto, Long ticketId) {
        Comment comment = commentConverter.fromCommentDto(commentDto);

        Ticket ticket = ticketService.findById(ticketId);

        comment.setDate(LocalDateTime.now());
        comment.setUser(ticket.getTicketOwner());
        comment.setTicket(ticket);

        commentRepository.save(comment);

        LOGGER.info("New commit has just been added to ticket {}: {}", ticketId, comment.getText());

        return comment;
    }

    @Override
    @Transactional
    public List<CommentDto> getAllByTicketId(Long ticketId, String buttonValue) {
        List<Comment> comments;

        if (buttonValue.equals("Show All")) {
            comments = commentRepository.findAllByTicketId(ticketId);

            LOGGER.info("All comments for ticket {}: {}", ticketId, comments);
        } else {
            comments = commentRepository.findAllByTicketId(ticketId, PageRequest.of(0, 5,
                    Sort.by(Sort.Direction.DESC, "date")));

            LOGGER.info("Last 5 comments for ticket {}: {}", ticketId, comments);
        }

        return comments.stream()
                .map(commentConverter::convertToCommentDto)
                .collect(Collectors.toList());
    }
}
