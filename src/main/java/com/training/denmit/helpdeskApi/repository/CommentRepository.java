package com.training.denmit.helpdeskApi.repository;

import com.training.denmit.helpdeskApi.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    List<Comment> findAllByTicketId(Long ticketId);

    List<Comment> findAllByTicketId(Long ticketId, Pageable pageable);
}
