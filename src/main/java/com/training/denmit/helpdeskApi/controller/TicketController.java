package com.training.denmit.helpdeskApi.controller;

import com.training.denmit.helpdeskApi.dto.history.HistoryDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketCreationDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketListViewDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketViewDto;
import com.training.denmit.helpdeskApi.model.Ticket;
import com.training.denmit.helpdeskApi.service.HistoryService;
import com.training.denmit.helpdeskApi.service.TicketService;
import com.training.denmit.helpdeskApi.service.ValidationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@AllArgsConstructor
@RequestMapping(value = "/tickets")
@Api("Ticket controller")
public class TicketController {

    private final TicketService ticketService;
    private final HistoryService historyService;
    private final ValidationService validationService;

    @PostMapping
    @ApiOperation(value = "Create a new ticket", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> save(Principal principal,
                                  @RequestBody @Valid TicketCreationDto ticketCreationDto,
                                  @RequestParam(value = "buttonValue", defaultValue = "default") String buttonValue) {
        Ticket savedTicket = ticketService.save(ticketCreationDto, principal.getName(), buttonValue);

        String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        String savedTicketLocation = currentUri + "/" + savedTicket.getId();

        return ResponseEntity.status(CREATED)
                .header(HttpHeaders.LOCATION, savedTicketLocation)
                .body(savedTicket);
    }

    @GetMapping("/{ticketId}")
    @ApiOperation(value = "Get ticket by ID", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<TicketViewDto> getById(@PathVariable("ticketId") Long ticketId) {
        return ResponseEntity.ok(ticketService.getById(ticketId));
    }

    @GetMapping("/all")
    @ApiOperation(value = "Get all tickets", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> getAll(Principal principal,
                                    @RequestParam(value = "parameter", defaultValue = "") String parameter,
                                    @RequestParam(value = "sortField", defaultValue = "default") String sortField,
                                    @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
                                    @RequestParam(value = "pageSize", defaultValue = "100") int pageSize,
                                    @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber) {
        String errorMessage = validationService.getWrongSearchParameterError(parameter);

        if (checkErrors(errorMessage)) {
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }

        List<TicketListViewDto> tickets = ticketService.getAll(principal.getName(), parameter, sortField,
                sortDirection, pageSize, pageNumber);

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/my")
    @ApiOperation(value = "Get own tickets", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> getMy(Principal principal,
                                   @RequestParam(value = "parameter", defaultValue = "") String parameter,
                                   @RequestParam(value = "sortField", defaultValue = "default") String sortField,
                                   @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
                                   @RequestParam(value = "pageSize", defaultValue = "100") int pageSize,
                                   @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber) {
        String errorMessage = validationService.getWrongSearchParameterError(parameter);

        if (checkErrors(errorMessage)) {
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }

        List<TicketListViewDto> tickets = ticketService.getOwn(principal.getName(), parameter, sortField,
                sortDirection, pageSize, pageNumber);

        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update ticket by ID", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> update(Principal principal,
                                    @PathVariable("id") Long ticketId,
                                    @Valid @RequestBody TicketCreationDto ticketEditDto,
                                    @RequestParam(value = "buttonValue", defaultValue = "default") String buttonValue) {
        Ticket updatedTicket = ticketService.update(ticketId, ticketEditDto, principal.getName(), buttonValue);

        return ResponseEntity.ok(updatedTicket);
    }

    @PutMapping("/{ticketId}/change-status")
    @ApiOperation(value = "Change ticket status", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> changeTicketStatus(Principal principal,
                                                @PathVariable("ticketId") Long ticketId,
                                                @RequestParam(value = "newStatus", required = false) String newStatus) {
        ticketService.changeTicketStatus(principal.getName(), ticketId, newStatus);

        return ResponseEntity.created(URI.create(String.format("/tickets/change-status/%s", ticketId))).build();
    }

    @GetMapping("{ticketId}/history")
    @ApiOperation(value = "Get all history with ticket")
    public ResponseEntity<List<HistoryDto>> getAllHistoryByTicketId(@PathVariable("ticketId") Long ticketId,
                                                                    @RequestParam(value = "buttonValue", defaultValue = "default") String buttonValue) {
        return ResponseEntity.ok(historyService.getAllByTicketId(ticketId, buttonValue));
    }

    @GetMapping("/next-ticket-id")
    @ApiOperation(value = "Get ID of a new ticket")
    public ResponseEntity<Long> getNewTicketId() {
        return ResponseEntity.ok(ticketService.getNewTicketId());
    }

    private boolean checkErrors(String errorMessage) {
        return !errorMessage.isEmpty();
    }
}
