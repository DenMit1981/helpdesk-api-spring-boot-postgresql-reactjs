package com.training.denmit.helpdeskApi.repository;

import com.training.denmit.helpdeskApi.model.Feedback;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends CrudRepository<Feedback, Long> {

    List<Feedback> findAllByTicketId(Long ticketId);

    List<Feedback> findAllByTicketId(Long ticketId, Pageable pageable);
}
