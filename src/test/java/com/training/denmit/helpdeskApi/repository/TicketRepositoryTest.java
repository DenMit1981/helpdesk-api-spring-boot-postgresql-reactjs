package com.training.denmit.helpdeskApi.repository;

import com.training.denmit.helpdeskApi.model.Ticket;
import com.training.denmit.helpdeskApi.model.User;
import com.training.denmit.helpdeskApi.model.enums.Category;
import com.training.denmit.helpdeskApi.model.enums.Role;
import com.training.denmit.helpdeskApi.model.enums.Status;
import com.training.denmit.helpdeskApi.model.enums.Urgency;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String[] CLEAN_TABLES_SQL = {
            "delete from comment",
            "delete from history",
            "delete from ticket",
            "delete from users"
    };

    @Before
    public void resetDb() {
        for (String query : CLEAN_TABLES_SQL) {
            jdbcTemplate.execute(query);
        }
    }

    @Test
    public void saveTest_thenReturnSavedTicket() {
        Ticket ticket = createTestTicket("new ticket", "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);

        Ticket savedTicket = ticketRepository.save(ticket);

        assertThat(savedTicket).isNotNull();
        assertThat(savedTicket.getId()).isGreaterThan(0);
    }

    @Test
    public void findByIdTest_thenReturnTicket() {
        Ticket ticket = createTestTicket("new ticket", "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);

        ticketRepository.save(ticket);

        Optional<Ticket> searchedTicket = ticketRepository.findById(ticket.getId());

        assertThat(searchedTicket).isNotEmpty();
    }

    @Test
    public void findById_NegativeTest_whenTicketDoesNotExist_thenReturnEmptiness() {
        Ticket ticket = createTestTicket("new ticket", "it's a new ticket",
                LocalDate.now(), Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL);

        ticketRepository.save(ticket);

        long wrongTicketId = ticket.getId() + 1L;

        Optional<Ticket> searchedTicket = ticketRepository.findById(wrongTicketId);

        assertThat(searchedTicket).isEmpty();
    }

    @Test
    public void findAllTest_thenReturnTicketsList() {
        User employee = createTestUser("Den", "Mit", "any@mail.com", "P@ssword1", Role.ROLE_EMPLOYEE);
        User manager = createTestUser("Peter", "Pen", "manager1@mail.com", "P@ssword1", Role.ROLE_MANAGER);
        User engineer = createTestUser("Olga", "Burkina", "engineer@mail.com", "P@ssword1", Role.ROLE_ENGINEER);

        createTestTicketList(employee, manager, engineer);

        List<Ticket> tickets = (List<Ticket>) ticketRepository.findAll();

        assertThat(tickets).isNotNull();
        assertThat(tickets.size()).isEqualTo(3);
    }

    @Test
    public void findAllByPageTest_IfPageSizeIsTwoAndPageNumberIsOne_thenReturnTicketsList() {
        User employee = createTestUser("Den", "Mit", "any@mail.com", "P@ssword1", Role.ROLE_EMPLOYEE);
        User manager = createTestUser("Peter", "Pen", "manager1@mail.com", "P@ssword1", Role.ROLE_MANAGER);
        User engineer = createTestUser("Olga", "Burkina", "engineer@mail.com", "P@ssword1", Role.ROLE_ENGINEER);

        createTestTicketList(employee, manager, engineer);

        List<Ticket> tickets = ticketRepository.findAll(PageRequest.of(0, 2));

        assertThat(tickets).isNotNull();
        assertThat(tickets.size()).isEqualTo(2);
    }

    @Test
    public void findAllForEmployeeByParameterTest_thenReturnTicketsList() {
        User employee = createTestUser("Den", "Mit", "any@mail.com", "P@ssword1", Role.ROLE_EMPLOYEE);
        User manager = createTestUser("Peter", "Pen", "manager1@mail.com", "P@ssword1", Role.ROLE_MANAGER);
        User engineer = createTestUser("Olga", "Burkina", "engineer@mail.com", "P@ssword1", Role.ROLE_ENGINEER);

        createTestTicketList(employee, manager, engineer);

        List<Ticket> tickets = ticketRepository.findAllForEmployeeByParameter(employee.getId(), "new ticket", PageRequest.of(0, 5));

        assertThat(tickets).isNotNull();
        assertThat(tickets.size()).isEqualTo(1);
    }

    @Test
    public void findAllForManagerByParameterTest_thenReturnTicketsList() {
        User manager = createTestUser("Peter", "Pen", "manager1@mail.com", "P@ssword1", Role.ROLE_MANAGER);
        User engineer = createTestUser("Olga", "Burkina", "engineer@mail.com", "P@ssword1", Role.ROLE_ENGINEER);

        createTestTicketList(manager, manager, engineer);

        List<Ticket> tickets = ticketRepository.findAllForManagerByParameter(manager.getId(), "", PageRequest.of(0, 5));


        assertThat(tickets).isNotNull();
        assertThat(tickets.size()).isEqualTo(3);
    }

    @Test
    public void findOwnForEngineerByParameterTest_thenReturnTicketsList() {
        User employee = createTestUser("Den", "Mit", "any@mail.com", "P@ssword1", Role.ROLE_EMPLOYEE);
        User manager = createTestUser("Peter", "Pen", "manager1@mail.com", "P@ssword1", Role.ROLE_MANAGER);
        User engineer = createTestUser("Olga", "Burkina", "engineer@mail.com", "P@ssword1", Role.ROLE_ENGINEER);

        createTestTicketList(employee, manager, engineer);

        List<Ticket> tickets = ticketRepository.findOwnForEngineerByParameter(engineer.getId(), "great ticket", PageRequest.of(0, 5));

        assertThat(tickets).isNotNull();
        assertThat(tickets.size()).isEqualTo(1);
    }

    @Test
    public void checkAccessToFeedbackTicketTest() {
        User employee = createTestUser("Den", "Mit", "any@mail.com", "P@ssword1", Role.ROLE_EMPLOYEE);
        User manager = createTestUser("Peter", "Pen", "manager1@mail.com", "P@ssword1", Role.ROLE_MANAGER);
        User engineer = createTestUser("Olga", "Burkina", "engineer@mail.com", "P@ssword1", Role.ROLE_ENGINEER);

        Ticket ticket = createTestTicketWithUsers("done ticket", "it's a done ticket", LocalDate.now(),
                Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL, Status.DONE, employee, manager, engineer);

        ticketRepository.save(ticket);

        ticketRepository.checkAccessToFeedbackTicket(employee.getId(), ticket.getId());
    }

    private Ticket createTestTicket(String name, String description, LocalDate desiredResolutionDate,
                                    Category category, Urgency urgency) {
        Ticket ticket = new Ticket();

        ticket.setName(name);
        ticket.setDescription(description);
        ticket.setDesiredResolutionDate(desiredResolutionDate);
        ticket.setCategory(category);
        ticket.setUrgency(urgency);

        return ticket;
    }

    private Ticket createTestTicketWithUsers(String name, String description, LocalDate desiredResolutionDate, Category category,
                                             Urgency urgency, Status status, User ticketOwner, User approver, User assignee) {
        Ticket ticket = new Ticket();

        ticket.setName(name);
        ticket.setDescription(description);
        ticket.setDesiredResolutionDate(desiredResolutionDate);
        ticket.setCategory(category);
        ticket.setUrgency(urgency);
        ticket.setStatus(status);
        ticket.setTicketOwner(ticketOwner);
        ticket.setApprover(approver);
        ticket.setAssignee(assignee);

        return ticket;
    }

    private void createTestTicketList(User ticketOwner, User approver, User assignee) {
        Ticket ticketOne = createTestTicketWithUsers("new ticket", "it's a new ticket", LocalDate.now(),
                Category.APPLICATION_AND_SERVICES, Urgency.CRITICAL, Status.NEW, ticketOwner, approver, assignee);

        Ticket ticketTwo = createTestTicketWithUsers("one more ticket", "again ticket", LocalDate.now(),
                Category.BENEFITS_AND_PAPER_WORK, Urgency.AVERAGE, Status.DRAFT, ticketOwner, approver, assignee);

        Ticket ticketThree = createTestTicketWithUsers("great ticket", "my ticket", LocalDate.now(),
                Category.HARDWARE_AND_SOFTWARE, Urgency.LOW, Status.NEW, ticketOwner, approver, assignee);

        ticketRepository.save(ticketOne);
        ticketRepository.save(ticketTwo);
        ticketRepository.save(ticketThree);
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
