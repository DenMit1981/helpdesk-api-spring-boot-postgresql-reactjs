package com.training.denmit.helpdeskApi.service.impl;

import com.training.denmit.helpdeskApi.converter.TicketConverter;
import com.training.denmit.helpdeskApi.dto.ticket.TicketCreationDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketListViewDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketViewDto;
import com.training.denmit.helpdeskApi.exception.AccessDeniedException;
import com.training.denmit.helpdeskApi.exception.TicketNotFoundException;
import com.training.denmit.helpdeskApi.mail.service.impl.EmailGenerateServiceImpl;
import com.training.denmit.helpdeskApi.mail.service.impl.EmailServiceImpl;
import com.training.denmit.helpdeskApi.model.Ticket;
import com.training.denmit.helpdeskApi.model.User;
import com.training.denmit.helpdeskApi.model.enums.Category;
import com.training.denmit.helpdeskApi.model.enums.Role;
import com.training.denmit.helpdeskApi.model.enums.Status;
import com.training.denmit.helpdeskApi.model.enums.Urgency;
import com.training.denmit.helpdeskApi.repository.TicketRepository;
import com.training.denmit.helpdeskApi.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {
    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private TicketConverter ticketConverter;

    @Mock
    HistoryServiceImpl historyService;

    @Mock
    EmailServiceImpl emailService;

    @Mock
    EmailGenerateServiceImpl emailGenerateService;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Test
    public void saveNewTicketTest() {
        User user = createTestUser("Den", "Mit", "any@mail.com", "P@ssword1", Role.ROLE_EMPLOYEE);

        TicketCreationDto ticketDto = createTestTicketDto("new ticket", "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);

        when(userService.getByLogin(user.getEmail())).thenReturn(user);

        Ticket ticket = getTestTicketFromTicketCreationDto(ticketDto);

        when(ticketConverter.fromTicketCreationDto(ticketDto)).thenReturn(ticket);

        willDoNothing().given(historyService).saveHistoryForCreatedTicket(ticket);

        Ticket result = ticketService.save(ticketDto, user.getEmail(), "SaveAsDraft");

        Assert.assertNotNull(result);
        Assert.assertEquals(ticket.getName(), result.getName());

        verify(ticketRepository, times(1)).save(ticketConverter.fromTicketCreationDto(ticketDto));
        verify(userService, times((2))).getByLogin(user.getEmail());
    }

    @Test
    public void saveNewTicketNegativeTest_IfUserRoleIsEngineer_ThenStatus403Forbidden() {
        User user = createTestUser("Peter", "Pen", "engineer@mail.com", "P@ssword1", Role.ROLE_ENGINEER);

        when(userService.getByLogin(user.getEmail())).thenReturn(user);

        TicketCreationDto ticketDto = createTestTicketDto("new ticket", "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);

        String error = "You can't have access to create ticket";

        assertThrows(AccessDeniedException.class,
                () -> ticketService.save(ticketDto, user.getEmail(), ""));

        try {
            ticketService.save(ticketDto, user.getEmail(), "");
        } catch (AccessDeniedException e) {
            if (e.getMessage().equals(error)) {
                return;
            }
        }
        Assert.fail();
    }

    @Test
    public void getTicketById_ThenReturnTicket() {
        Ticket ticket = createTestTicket("new ticket", "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);

        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        TicketViewDto expected = ticketConverter.convertToTicketViewDto(ticket);

        TicketViewDto actual = ticketService.getById(ticket.getId());

        Assert.assertEquals(expected, actual);

        verify(ticketRepository, times(1)).findById(ticket.getId());
    }

    @Test(expected = TicketNotFoundException.class)
    public void getTicketById_IfTicketNotFound_ThenReturnException() {
        List<Ticket> tickets = (List<Ticket>) ticketRepository.findAll();
        Long wrongTicketId = tickets.size() + 1L;

        ticketService.getById(wrongTicketId);

        verify(ticketRepository, times(1)).findById(wrongTicketId);
    }

    @Test
    public void getAllTicketsForEmployeeTest() {
        User user = createTestUser("Peter", "Pen", "employee@mail.com", "P@ssword1", Role.ROLE_EMPLOYEE);

        when(userService.getByLogin(user.getEmail())).thenReturn(user);

        Ticket ticketOne = createTestTicket("new ticket", "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);
        Ticket ticketTwo = createTestTicket("one more ticket", "again ticket",
                LocalDate.now(), Category.BENEFITS_AND_PAPER_WORK, Urgency.AVERAGE);
        Ticket ticketThree = createTestTicket("great ticket", "my ticket",
                LocalDate.now(), Category.HARDWARE_AND_SOFTWARE, Urgency.LOW);

        List<Ticket> tickets = asList(ticketOne, ticketTwo, ticketThree);
        System.out.println(tickets);

        List<TicketListViewDto> expected = tickets.stream()
                .map(ticketConverter::convertToTicketListViewDto)
                .collect(Collectors.toList());
        System.out.println(expected.size());

        when(ticketRepository.findAllForEmployeeByParameter(user.getId(),
                "", PageRequest.of(0, 10))).thenReturn(tickets);

        List<TicketListViewDto> actual = ticketService.getAll(user.getEmail(),
                "", "default", "asc", 10, 1);

        Assert.assertEquals(3, actual.size());
        Assert.assertEquals(3, expected.size());
        Assert.assertEquals(expected, actual);

        verify(ticketRepository, times(1)).findAllForEmployeeByParameter(user.getId(),
                "", PageRequest.of(0, 10));
        verify(userService, times(1)).getByLogin(user.getEmail());
    }

    @Test
    public void getOwnTicketsForManagerTest() {
        User user = createTestUser("Peter", "Pen", "manager@mail.com", "P@ssword1", Role.ROLE_MANAGER);

        when(userService.getByLogin(user.getEmail())).thenReturn(user);

        Ticket ticketOne = createTestTicket("new ticket", "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);
        Ticket ticketTwo = createTestTicket("one more ticket", "again ticket",
                LocalDate.now(), Category.BENEFITS_AND_PAPER_WORK, Urgency.AVERAGE);
        Ticket ticketThree = createTestTicket("great ticket", "my ticket",
                LocalDate.now(), Category.HARDWARE_AND_SOFTWARE, Urgency.LOW);

        List<Ticket> tickets = asList(ticketOne, ticketTwo, ticketThree);
        System.out.println(tickets);

        List<TicketListViewDto> expected = tickets.stream()
                .map(ticketConverter::convertToTicketListViewDto)
                .collect(Collectors.toList());
        System.out.println(expected.size());

        when(ticketRepository.findOwnForManagerByParameter(user.getId(),
                "", PageRequest.of(0, 10))).thenReturn(tickets);

        List<TicketListViewDto> actual = ticketService.getOwn(user.getEmail(),
                "", "default", "asc", 10, 1);

        Assert.assertEquals(3, actual.size());
        Assert.assertEquals(3, expected.size());
        Assert.assertEquals(expected, actual);

        verify(ticketRepository, times(1)).findOwnForManagerByParameter(user.getId(),
                "", PageRequest.of(0, 10));
        verify(userService, times(1)).getByLogin(user.getEmail());
    }

    @Test
    public void updateTicketTest() {
        User user = createTestUser("Peter", "Pen", "employee@mail.com", "P@ssword1", Role.ROLE_EMPLOYEE);
        Ticket ticket = createTestTicket("new ticket", "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);

        TicketCreationDto ticketDto = createTestTicketDto("new ticket", "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);

        List<Ticket> tickets = (List<Ticket>) ticketRepository.findAll();

        Long ticketId = (long) tickets.size();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        willDoNothing().given(ticketConverter).editTicketFromTicketCreationDto(ticket, ticketDto);
        willDoNothing().given(historyService).saveHistoryForUpdatedTicket(ticket);
        willDoNothing().given(emailService).sendNewTicketMail(ticketId);

        Ticket result = ticketService.update(ticketId, ticketDto, user.getEmail(), "");

        Assert.assertNotNull(result);
        Assert.assertEquals(ticket, result);

        verify(ticketRepository, atLeast(1)).save(ticket);
    }

    @Test
    public void changeTicketStatusTest() {
        User user = createTestUser("Peter", "Pen", "employee@mail.com", "P@ssword1", Role.ROLE_EMPLOYEE);

        Ticket ticket = createTestTicket("new ticket", "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);
        ticket.setStatus(Status.DRAFT);
        ticket.setTicketOwner(user);

        Status previousStatus = ticket.getStatus();
        Status newStatus = Status.NEW;

        List<Ticket> tickets = (List<Ticket>) ticketRepository.findAll();
        Long ticketId = (long) tickets.size();

        when(userService.getByLogin(user.getEmail())).thenReturn(user);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        willDoNothing().given(historyService).saveHistoryForChangedTicketStatus(ticket, previousStatus, newStatus);

        ticketService.changeTicketStatus(user.getEmail(), ticketId, String.valueOf(newStatus));
    }

    @Test
    public void changeTicketStatusNeagativeTest() {
        User user = createTestUser("Peter", "Pen", "employee@mail.com", "P@ssword1", Role.ROLE_EMPLOYEE);

        Ticket ticket = createTestTicket("new ticket", "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);
        ticket.setStatus(Status.DRAFT);
        ticket.setTicketOwner(user);

        Status previousStatus = ticket.getStatus();
        Status newStatus = Status.NEW;

        List<Ticket> tickets = (List<Ticket>) ticketRepository.findAll();
        Long ticketId = (long) tickets.size();

        when(userService.getByLogin(user.getEmail())).thenReturn(user);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        willDoNothing().given(historyService).saveHistoryForChangedTicketStatus(ticket, previousStatus, newStatus);

        ticketService.changeTicketStatus(user.getEmail(), ticketId, String.valueOf(newStatus));
    }

    private Ticket createTestTicket(String name, String description, LocalDate desiredResolutionDate,
                                    Category category, Urgency urgency) {
        Ticket ticket = new Ticket();

        ticket.setName(name);
        ticket.setDescription(description);
        ticket.setDesiredResolutionDate(desiredResolutionDate);
        ticket.setCategory(category);
        ticket.setUrgency(urgency);

        ticketRepository.save(ticket);

        return ticket;
    }

    private TicketCreationDto createTestTicketDto(String name, String description, LocalDate desiredResolutionDate,
                                                  Category category, Urgency urgency) {
        TicketCreationDto ticketCreationDto = new TicketCreationDto();

        ticketCreationDto.setName(name);
        ticketCreationDto.setDescription(description);
        ticketCreationDto.setDesiredResolutionDate(desiredResolutionDate);
        ticketCreationDto.setCategory(category);
        ticketCreationDto.setUrgency(urgency);

        return ticketCreationDto;
    }

    private Ticket getTestTicketFromTicketCreationDto(TicketCreationDto ticketCreationDto) {
        Ticket ticket = new Ticket();

        ticket.setName(ticketCreationDto.getName());
        ticket.setDescription(ticketCreationDto.getDescription());
        ticket.setDesiredResolutionDate(ticketCreationDto.getDesiredResolutionDate());
        ticket.setCategory(ticketCreationDto.getCategory());
        ticket.setUrgency(ticketCreationDto.getUrgency());

        return ticket;
    }

    private User createTestUser(String firstName, String lastName, String email, String password, Role role) {
        User user = new User();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        userRepository.save(user);

        return user;
    }
}
