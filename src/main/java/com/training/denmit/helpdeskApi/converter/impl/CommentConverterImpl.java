package com.training.denmit.helpdeskApi.converter.impl;

import com.training.denmit.helpdeskApi.converter.CommentConverter;
import com.training.denmit.helpdeskApi.dto.comment.CommentDto;
import com.training.denmit.helpdeskApi.model.Comment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommentConverterImpl implements CommentConverter {

    @Override
    public CommentDto convertToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();

        commentDto.setDate(comment.getDate());
        commentDto.setUser(comment.getUser().getLastName() + " " + comment.getUser().getFirstName());
        commentDto.setText(comment.getText());

        return commentDto;
    }

    @Override
    public Comment fromCommentDto(CommentDto commentDto) {
        Comment comment = new Comment();

        comment.setText(commentDto.getText());

        return comment;
    }
}
