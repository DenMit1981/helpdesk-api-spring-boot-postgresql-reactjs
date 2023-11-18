package com.training.denmit.helpdeskApi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.training.denmit.helpdeskApi.model.enums.Category;
import com.training.denmit.helpdeskApi.model.enums.Status;
import com.training.denmit.helpdeskApi.model.enums.Urgency;
import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "ticket")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Ticket implements Serializable {

    @Serial
    private static final long serialVersionUID = 3906771677381811334L;

    @Id
    @SequenceGenerator(name = "ticketIdSeq", sequenceName = "ticket_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticketIdSeq")
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdOn;

    @Column(name = "desired_resolution_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate desiredResolutionDate;

    @ManyToOne(fetch = FetchType.EAGER,
            cascade = CascadeType.DETACH)
    @JoinColumn(name = "owner_id")
    private User ticketOwner;

    @ManyToOne(fetch = FetchType.EAGER,
            cascade = CascadeType.DETACH)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.EAGER,
            cascade = CascadeType.DETACH)
    @JoinColumn(name = "approver_id")
    private User approver;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "urgency")
    @Enumerated(EnumType.STRING)
    private Urgency urgency;
}
