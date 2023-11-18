package com.training.denmit.helpdeskApi.repository;

import com.training.denmit.helpdeskApi.model.Attachment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends CrudRepository<Attachment, Long> {

    Optional<Attachment> findByIdAndTicketId(Long id, Long ticketId);

    List<Attachment> findAllByTicketId(Long ticketId);

    void deleteByNameAndTicketId(String name, Long ticketId);

    void deleteByIdAndTicketId(Long id, Long ticketId);
}
