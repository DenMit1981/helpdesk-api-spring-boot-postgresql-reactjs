package com.training.denmit.helpdeskApi.repository;

import com.training.denmit.helpdeskApi.model.Ticket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends CrudRepository<Ticket, Long> {

    String QUERY_FIND_ALL = "from Ticket t";
    String QUERY_FIND_ALL_FOR_EMPLOYEE = "from Ticket t where t.ticketOwner.id =:employeeId and t.ticketOwner.role like 'ROLE_EMPLOYEE'";
    String QUERY_FIND_ALL_FOR_MANAGER = "from Ticket t where ((t.ticketOwner.id =:managerId and t.ticketOwner.role like 'ROLE_MANAGER') " +
            "or (t.ticketOwner.role like 'ROLE_EMPLOYEE' and t.status like 'NEW')" +
            "or (t.approver.id =:managerId and t.status in ('APPROVED', 'DECLINED', 'CANCELED', 'IN_PROGRESS', 'DONE')))";
    String QUERY_FIND_OWN_FOR_MANAGER = "from Ticket t where ((t.ticketOwner.id =:managerId and t.ticketOwner.role like 'ROLE_MANAGER') " +
            "or (t.approver.id =:managerId and t.status like 'APPROVED'))";
    String QUERY_FIND_ALL_FOR_ENGINEER = "from Ticket t where ((t.ticketOwner.role in ('ROLE_EMPLOYEE', 'ROLE_MANAGER') " +
            "and t.status like 'APPROVED') or (t.assignee.id =:engineerId and t.status in ('IN_PROGRESS', 'DONE')))";
    String QUERY_FIND_OWN_FOR_ENGINEER = "from Ticket t where t.assignee.id =:engineerId";
    String FILTER_BY = " and (str(t.id) like concat('%',:parameter,'%') " +
            "or lower(t.name) like lower (concat('%',:parameter,'%')) " +
            "or str(t.desiredResolutionDate) like concat ('%',:parameter,'%') " +
            "or lower(str(t.urgency)) like lower(concat('%',:parameter,'%')) " +
            "or lower(str(t.status)) like lower(concat('%',:parameter,'%')))";
    String QUERY_FIND_FROM_DONE_TICKET = "from Ticket t where t.id = :ticketId and t.ticketOwner.id = :userId and t.status = 'DONE' " +
            "and t.ticketOwner.role in ('ROLE_EMPLOYEE', 'ROLE_MANAGER')";

    @Query(QUERY_FIND_ALL)
    List<Ticket> findAll(Pageable pageable);

    @Query(QUERY_FIND_ALL_FOR_EMPLOYEE + FILTER_BY)
    List<Ticket> findAllForEmployeeByParameter(@Param("employeeId") Long employeeId,
                                               @Param("parameter") String parameter,
                                               Pageable pageable);

    @Query(QUERY_FIND_ALL_FOR_MANAGER + FILTER_BY)
    List<Ticket> findAllForManagerByParameter(@Param("managerId") Long managerId,
                                              @Param("parameter") String parameter,
                                              Pageable pageable);

    @Query(QUERY_FIND_OWN_FOR_MANAGER + FILTER_BY)
    List<Ticket> findOwnForManagerByParameter(@Param("managerId") Long managerId,
                                              @Param("parameter") String parameter,
                                              Pageable pageable);

    @Query(QUERY_FIND_ALL_FOR_ENGINEER + FILTER_BY)
    List<Ticket> findAllForEngineerByParameter(@Param("engineerId") Long engineerId,
                                               @Param("parameter") String parameter,
                                               Pageable pageable);

    @Query(QUERY_FIND_OWN_FOR_ENGINEER + FILTER_BY)
    List<Ticket> findOwnForEngineerByParameter(@Param("engineerId") Long engineerId,
                                               @Param("parameter") String parameter,
                                               Pageable pageable);

    @Query(QUERY_FIND_FROM_DONE_TICKET)
    Optional<Ticket> checkAccessToFeedbackTicket(@Param("userId") Long userId,
                                                 @Param("ticketId") Long ticketId);
}

