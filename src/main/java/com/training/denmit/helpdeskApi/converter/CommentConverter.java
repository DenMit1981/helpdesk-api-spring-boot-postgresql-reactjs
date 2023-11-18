package com.training.denmit.helpdeskApi.converter;

import com.training.denmit.helpdeskApi.dto.comment.CommentDto;
import com.training.denmit.helpdeskApi.model.Comment;

public interface CommentConverter {

    CommentDto convertToCommentDto(Comment comment);

    Comment fromCommentDto(CommentDto commentDto);
}
