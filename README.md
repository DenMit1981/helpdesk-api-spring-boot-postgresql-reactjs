1.Name of project: helpdesk-api-spring-boot-postgresql-reactjs

2.Launch of project:
2.1 backend part:
  Build:
     docker-compose build
  Run:
     docker-compose up
2.2 frontend part:
  Build:
     \src\frontend-react\java-learn-app-main>npm install
  Run:
     \src\frontend-react\java-learn-app-main>npm start

3.Ports of the project:
    backend: http://localhost:8081
    frontend: http://localhost:3000

4.Start page: http://localhost:3000

5.Sender's email: "denmit777@mail.ru"

6.Logins (recipients' emails) and passwords of users:

Role 'Employee':
user1_mogilev@yopmail.com/P@ssword1
user2_mogilev@yopmail.com/P@ssword1

Role 'Manager':
manager1_mogilev@yopmail.com/P@ssword1
manager2_mogilev@yopmail.com/P@ssword1

Role 'Engineer':
engineer1_mogilev@yopmail.com/P@ssword1
engineer2_mogilev@yopmail.com/P@ssword1

7.Configuration: resources/application.properties

8.Templates: resources/templates

9.Database scripts: resources/data.sql

10.Database PostgreSQL connection:
  Name: helpdeskdb@localhost
  User: denmit
  Password: 1981
  Port: 5432

11.Launch of all the tests from command line:
    EditConfiguration -> JUnit -> name:mvn test -> All In Directory: helpdesk-api-spring-boot-postgresql-reactjs\src\test ->
    Environment variables : clean test

12.Rest controllers:

AuthController:
registerUser(POST): http://localhost:8081 + body;
authenticationUser(POST): http://localhost:8081/auth + body

TicketController:
save(POST): http://localhost:8081/tickets + body;
getById(GET): http://localhost:8081/tickets/{ticketId};
getAll(GET): http://localhost:8081/tickets/all
getMy(GET): http://localhost:8081/tickets/my
update(PUT): http://localhost:8081/tickets/{id} + body;
changeTicketStatus(PUT): http://localhost:8081/tickets/{ticketId}/change-status + parameters
getAllHistoryByTicketId(GET): http://localhost:8081/tickets/{ticketId}/history;
getNewTicketId(GET): http://localhost:8081/tickets/next-ticket-id;

AttachmentController:
uploadFile(POST): http://localhost:8081/tickets/{ticketId}/attachments + body;
getById(GET): http://localhost:8081/tickets/{ticketId}/attachments/{attachmentId};
getAllByTicketId(GET): http://localhost:8081/tickets/{ticketId}/attachments
deleteFile(DELETE): http://localhost:8081/tickets/{ticketId}/attachments/{attachmentName};

CommentController:
save(POST): http://localhost:8081/tickets/{ticketId}/comments + body;
getAllByTicketId(GET): http://localhost:8081/tickets/{ticketId}/comments

FeedbackController:
save(POST): http://localhost:8081/tickets/{ticketId}/feedbacks + body;
getAllByTicketId(GET): http://localhost:8081/tickets/{ticketId}/feedbacks


