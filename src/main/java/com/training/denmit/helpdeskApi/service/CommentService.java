package com.training.denmit.helpdeskApi.service;

import com.training.denmit.helpdeskApi.dto.comment.CommentDto;
import com.training.denmit.helpdeskApi.model.Comment;

import java.util.List;

public interface CommentService {

    Comment save(CommentDto commentDto, Long ticketId);

    List<CommentDto> getAllByTicketId(Long ticketId, String buttonValue);
}
