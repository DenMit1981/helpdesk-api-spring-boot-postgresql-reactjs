package com.training.denmit.helpdeskApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.denmit.helpdeskApi.dto.history.HistoryDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketCreationDto;
import com.training.denmit.helpdeskApi.dto.ticket.TicketListViewDto;
import com.training.denmit.helpdeskApi.dto.user.UserRegisterDto;
import com.training.denmit.helpdeskApi.model.Ticket;
import com.training.denmit.helpdeskApi.model.User;
import com.training.denmit.helpdeskApi.model.enums.Category;
import com.training.denmit.helpdeskApi.model.enums.Urgency;
import com.training.denmit.helpdeskApi.service.HistoryService;
import com.training.denmit.helpdeskApi.service.TicketService;
import com.training.denmit.helpdeskApi.service.UserService;
import com.training.denmit.helpdeskApi.service.ValidationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String[] CLEAN_TABLES_SQL = {
            "delete from comment",
            "delete from history",
            "delete from ticket",
            "delete from users"
    };

    @AfterEach
    public void resetDb() {
        for (String query : CLEAN_TABLES_SQL) {
            jdbcTemplate.execute(query);
        }
    }

    @Test
    @WithMockUser(username = "user@mail.com", password = "P@ssword1")
    void saveTest_withStatus201andTicketReturned() throws Exception {
        createTestUser("Den", "Mit", "user@mail.com", "P@ssword1");

        TicketCreationDto ticketDto = createTestTicketDto("new ticket", "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);

        mockMvc.perform(
                        post("http://localhost:8081/tickets")
                                .content(objectMapper.writeValueAsString(ticketDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(ticketDto.getName()))
                .andExpect(jsonPath("$.description").value(ticketDto.getDescription()))
                .andExpect(jsonPath("$.desiredResolutionDate").value(String.valueOf(ticketDto.getDesiredResolutionDate())))
                .andExpect(jsonPath("$.category").value(ticketDto.getCategory().name()))
                .andExpect(jsonPath("$.urgency").value(ticketDto.getUrgency().name()));
    }

    @Test
    @WithMockUser(username = "user@mail.com", password = "P@ssword1")
    void save_NegativeTest_whenCreateInvalidTicket_thenStatus400BadRequest() throws Exception {
        String wrongTicketName = "";

        createTestUser("Den", "Mit", "user@mail.com", "P@ssword1");

        TicketCreationDto ticketDto = createTestTicketDto(wrongTicketName, "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);

        mockMvc.perform(
                        post("http://localhost:8081/tickets")
                                .content(objectMapper.writeValueAsString(ticketDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "engineer@mail.com", password = "P@ssword1")
    void save_NegativeTest_whenEngineerDoesNotHaveAccessToCreateTicket_thenStatus403Forbidden() throws Exception {
        createTestUser("Den", "Mit", "engineer@mail.com", "P@ssword1");

        TicketCreationDto ticketDto = createTestTicketDto("new ticket", "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);

        String error = "You can't have access to create ticket";

        mockMvc.perform(
                        post("http://localhost:8081/tickets")
                                .content(objectMapper.writeValueAsString(ticketDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.info").value(error));
    }

    @Test
    @WithMockUser(username = "manager@mail.com", password = "P@ssword1")
    void getByIdTest_withStatus200andTicketReturned() throws Exception {
        User user = createTestUser("Den", "Mit", "manager@mail.com", "P@ssword1");

        Ticket ticket = createTestTicket("ticket", "yes", LocalDate.now(), Category.APPLICATION_AND_SERVICES,
                Urgency.AVERAGE, user.getEmail(), "SaveAsDraft");

        mockMvc.perform(
                        get("http://localhost:8081/tickets/{ticketId}", ticket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(ticket.getName()))
                .andExpect(jsonPath("$.createdOn").value(String.valueOf(ticket.getCreatedOn())))
                .andExpect(jsonPath("$.status").value(ticket.getStatus().name()))
                .andExpect(jsonPath("$.urgency").value(ticket.getUrgency().name()))
                .andExpect(jsonPath("$.desiredResolutionDate").value(String.valueOf(ticket.getDesiredResolutionDate())))
                .andExpect(jsonPath("$.ticketOwner").value(ticket.getTicketOwner().getLastName() +
                        " " + ticket.getTicketOwner().getFirstName()))
                .andExpect(jsonPath("$.description").value(ticket.getDescription()))
                .andExpect(jsonPath("$.category").value(ticket.getCategory().name()));
    }

    @Test
    @WithMockUser(username = "manager@mail.com", password = "P@ssword1")
    void getById_NegativeTest_whenGetNotExistingTicket_thenStatus404NotFound() throws Exception {
        User user = createTestUser("Den", "Mit", "manager@mail.com", "P@ssword1");

        long wrongTicketId = createTestTicket("ticket", "yes", LocalDate.now(), Category.APPLICATION_AND_SERVICES,
                Urgency.AVERAGE, user.getEmail(), "").getId() + 1L;

        String error = "Ticket with id " + wrongTicketId + " not found";

        mockMvc.perform(
                        get("http://localhost:8081/tickets/{id}", wrongTicketId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.info").value(error));
    }

    @Test
    @WithMockUser(username = "manager@mail.com", password = "P@ssword1")
    void updateTest_withStatus200andUpdatedTicketReturned() throws Exception {
        User user = createTestUser("Den", "Mit", "manager@mail.com", "P@ssword1");

        long ticketId = createTestTicket("ticket", "yes", LocalDate.now(), Category.APPLICATION_AND_SERVICES,
                Urgency.AVERAGE, user.getEmail(), "SaveAsDraft").getId();

        TicketCreationDto ticketDto = createTestTicketDto("updated ticket", "it's a updated ticket",
                LocalDate.now(), Category.BENEFITS_AND_PAPER_WORK, Urgency.HIGH);

        mockMvc.perform(
                        put("http://localhost:8081/tickets/{id}", ticketId)
                                .content(objectMapper.writeValueAsString(ticketDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(ticketDto.getName()))
                .andExpect(jsonPath("$.description").value(ticketDto.getDescription()))
                .andExpect(jsonPath("$.desiredResolutionDate").value(String.valueOf(ticketDto.getDesiredResolutionDate())))
                .andExpect(jsonPath("$.category").value(ticketDto.getCategory().name()))
                .andExpect(jsonPath("$.urgency").value(ticketDto.getUrgency().name()));
    }

    @Test
    @WithMockUser(username = "manager@mail.com", password = "P@ssword1")
    void getAllTest_withStatus200() throws Exception {
        User user = createTestUser("Den", "Mit", "manager@mail.com", "P@ssword1");

        List<TicketListViewDto> tickets = ticketService.getAll(user.getEmail(),
                "", "default", "", 25, 1);

        mockMvc.perform(
                        get("http://localhost:8081/tickets/all"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(tickets)));
    }

    @Test
    @WithMockUser(username = "manager@mail.com", password = "P@ssword1")
    void getAll_NegativeTest_whenSearchWrongParameters_thenStatus400BadRequest() throws Exception {
        createTestUser("Den", "Mit", "manager@mail.com", "P@ssword1");

        String invalidParameter = "тик&t%";
        String expectedError = "Search should be in latin letters or figures";
        String actualError = validationService.getWrongSearchParameterError(invalidParameter);

        mockMvc.perform(
                        get("http://localhost:8081/tickets/all?parameter=" + invalidParameter))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(expectedError,
                        actualError));
    }

    @Test
    @WithMockUser(username = "manager@mail.com", password = "P@ssword1")
    void getMyTest_withSearchAndSortParametersAndStatus200() throws Exception {
        User user = createTestUser("Den", "Mit", "manager@mail.com", "P@ssword1");

        List<TicketListViewDto> tickets = ticketService.getOwn(user.getEmail(),
                "t", "id", "desc", 10, 1);

        mockMvc.perform(
                        get("http://localhost:8081/tickets/my?pageSize=10&pageNumber=1&sortField=id&sortDirection=desc&searchField=name&parameter=t"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(tickets)));
    }

    @Test
    @WithMockUser(username = "manager@mail.com", password = "P@ssword1")
    void changeTicketStatusTest_whenChangeStatusFromNewToApproved_withStatus201() throws Exception {
        User ticketOwner = createTestUser("Den", "Mit", "user@mail.com", "P@ssword1");
        createTestUser("Bob", "Zee", "manager@mail.com", "P@ssword1");

        Ticket ticket = createTestTicket("ticket", "yes", LocalDate.now(), Category.APPLICATION_AND_SERVICES,
                Urgency.AVERAGE, ticketOwner.getEmail(), "");

        mockMvc.perform(
                        put("http://localhost:8081/tickets/{ticketId}/change-status?newStatus=approved", ticket.getId()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "manager@mail.com", password = "P@ssword1")
    void changeTicketStatus_NegativeTest_whenNoAccessToChangeStatusFromNewToDoneForManager_thenStatus403() throws Exception {
        User ticketOwner = createTestUser("Den", "Mit", "user@mail.com", "P@ssword1");
        createTestUser("Bob", "Zee", "manager@mail.com", "P@ssword1");

        Ticket ticket = createTestTicket("ticket", "yes", LocalDate.now(), Category.APPLICATION_AND_SERVICES,
                Urgency.AVERAGE, ticketOwner.getEmail(), "");

        String error = "You can't change status of current ticket";

        mockMvc.perform(
                        put("http://localhost:8081/tickets/{ticketId}/change-status?newStatus=done", ticket.getId()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.info").value(error));
    }

    @Test
    @WithMockUser(username = "manager@mail.com", password = "P@ssword1")
    void changeTicketStatus_NegativeTest_whenNoAccessToChangeStatusIfOwnTicket_thenStatus403() throws Exception {
        User ticketOwner = createTestUser("Den", "Mit", "manager@mail.com", "P@ssword1");

        Ticket ticket = createTestTicket("ticket", "yes", LocalDate.now(), Category.APPLICATION_AND_SERVICES,
                Urgency.AVERAGE, ticketOwner.getEmail(), "");

        String error = "You can't formatted your own ticket";

        mockMvc.perform(
                        put("http://localhost:8081/tickets/{ticketId}/change-status?newStatus=approved", ticket.getId()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.info").value(error));
    }

    @Test
    @WithMockUser(username = "den@mail.com", password = "P@ssword1")
    void getAllHistoryByTicketId_withStatus200andListOfHistoryReturned() throws Exception {
        User user = createTestUser("Den", "Mit", "den@mail.com", "P@ssword1");

        long ticketId = createTestTicket("ticket", "yes", LocalDate.now(), Category.APPLICATION_AND_SERVICES,
                Urgency.AVERAGE, user.getEmail(), "").getId();

        List<HistoryDto> history = historyService.getAllByTicketId(ticketId, "");

        mockMvc.perform(
                        get("http://localhost:8081/tickets/" + ticketId + "/history"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(history)));
    }

    private Ticket createTestTicket(String name, String description, LocalDate desiredResolutionDate,
                                    Category category, Urgency urgency, String login, String buttonValue) {
        TicketCreationDto ticket = new TicketCreationDto();

        ticket.setName(name);
        ticket.setDescription(description);
        ticket.setDesiredResolutionDate(desiredResolutionDate);
        ticket.setCategory(category);
        ticket.setUrgency(urgency);

        return ticketService.save(ticket, login, buttonValue);
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

    private User createTestUser(String firstName, String lastName, String email, String password) {
        UserRegisterDto userRegisterDto = new UserRegisterDto();

        userRegisterDto.setFirstName(firstName);
        userRegisterDto.setLastName(lastName);
        userRegisterDto.setEmail(email);
        userRegisterDto.setPassword(password);

        return userService.save(userRegisterDto);
    }
}