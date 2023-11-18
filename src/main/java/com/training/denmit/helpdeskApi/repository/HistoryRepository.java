package com.training.denmit.helpdeskApi.repository;

import com.training.denmit.helpdeskApi.model.History;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends CrudRepository<History, Long> {

    List<History> findAllByTicketIdOrderByDate(Long ticketId);

    List<History> findAllByTicketId(Long ticketId, Pageable pageable);
}
