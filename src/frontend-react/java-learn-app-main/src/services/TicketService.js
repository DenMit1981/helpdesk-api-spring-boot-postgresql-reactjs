import axios from 'axios';

const TICKET_API_BASE_URL = "http://localhost:8081/tickets"

class TicketService {

    createTicket(ticket) {
        return axios.post(TICKET_API_BASE_URL, ticket);
    }

    createTicketAsDraft(ticket) {
        return axios.post(TICKET_API_BASE_URL + '/?buttonValue=SaveAsDraft', ticket);
    }

    getTicketById(ticketId) {
         return axios.get(TICKET_API_BASE_URL + '/' + ticketId);
    }

    getAllTickets() {
        return axios.get(TICKET_API_BASE_URL + '/all');
    }

    getMyTickets() {
        return axios.get(TICKET_API_BASE_URL + '/my');
    }

    getAllTicketsByPages = (pageSize, pageNumber) => {
        return axios.get(TICKET_API_BASE_URL + '/all?pageSize=' + pageSize + '&pageNumber=' + pageNumber);
    }

    getAllSortedTicketsByPages = (sortField, pageSize, pageNumber) => {
        return axios.get(TICKET_API_BASE_URL + '/all?sortField=' + sortField + '&pageSize=' + pageSize + '&pageNumber=' + pageNumber);
    }

    getAllDescSortedTicketsByPages = (sortField, pageSize, pageNumber) => {
        return axios.get(TICKET_API_BASE_URL + '/all?sortField=' + sortField + '&sortDirection=desc&pageSize=' + pageSize + '&pageNumber=' + pageNumber);
    }

    getAllTicketsByPagesSearchedByParameter = (parameter, pageSize, pageNumber) => {
        return axios.get(TICKET_API_BASE_URL + '/all?parameter=' + parameter + '&pageSize=' + pageSize + '&pageNumber=' + pageNumber);
    }

    getAllSortedTicketsByPagesSearchedByParameter = (parameter, sortField, pageSize, pageNumber) => {
        return axios.get(TICKET_API_BASE_URL + '/all?parameter=' + parameter + '&sortField=' + sortField + '&pageSize=' + pageSize + '&pageNumber=' + pageNumber);
    }

    getAllDescSortedTicketsByPagesSearchedByParameter = (parameter, sortField, pageSize, pageNumber) => {
        return axios.get(TICKET_API_BASE_URL + '/all?parameter=' + parameter + '&sortField=' + sortField + '&sortDirection=desc&pageSize=' + pageSize + '&pageNumber=' + pageNumber);
    }

    getMyTicketsByPages = (pageSize, pageNumber) => {
        return axios.get(TICKET_API_BASE_URL + '/my?pageSize=' + pageSize + '&pageNumber=' + pageNumber);
    }

    getMySortedTicketsByPages = (sortField, pageSize, pageNumber) => {
        return axios.get(TICKET_API_BASE_URL + '/my?sortField=' + sortField + '&pageSize=' + pageSize + '&pageNumber=' + pageNumber);
    }

    getMyDescSortedTicketsByPages = (sortField, pageSize, pageNumber) => {
        return axios.get(TICKET_API_BASE_URL + '/my?sortField=' + sortField + '&sortDirection=desc&pageSize=' + pageSize + '&pageNumber=' + pageNumber);
    }

    getMyTicketsByPagesSearchedByParameter = (parameter, pageSize, pageNumber) => {
        return axios.get(TICKET_API_BASE_URL + '/my?parameter=' + parameter + '&pageSize=' + pageSize + '&pageNumber=' + pageNumber);
    }

    getMySortedTicketsByPagesSearchedByParameter = (parameter, sortField, pageSize, pageNumber) => {
        return axios.get(TICKET_API_BASE_URL + '/my?parameter=' + parameter + '&sortField=' + sortField + '&pageSize=' + pageSize + '&pageNumber=' + pageNumber);
    }

    getMyDescSortedTicketsByPagesSearchedByParameter = (parameter, sortField, pageSize, pageNumber) => {
        return axios.get(TICKET_API_BASE_URL + '/my?parameter=' + parameter + '&sortField=' + sortField + '&sortDirection=desc&pageSize=' + pageSize + '&pageNumber=' + pageNumber);
    }

    updateTicket(ticket, ticketId) {
        return axios.put(TICKET_API_BASE_URL + '/' + ticketId, ticket);
    }

    updateTicketAndSaveAsDraft(ticket, ticketId) {
        return axios.put(TICKET_API_BASE_URL + '/' + ticketId + '/?buttonValue=SaveAsDraft', ticket);
    }

    changeTicketStatusToNew(ticketId) {
        return axios.put(TICKET_API_BASE_URL + '/' + ticketId + '/change-status?newStatus=new');
    }

    changeTicketStatusToCanceled(ticketId) {
        return axios.put(TICKET_API_BASE_URL + '/' + ticketId + '/change-status?newStatus=canceled');
    }

    changeTicketStatusToApproved(ticketId) {
        return axios.put(TICKET_API_BASE_URL + '/' + ticketId + '/change-status?newStatus=approved');
    }

    changeTicketStatusToDeclined(ticketId) {
        return axios.put(TICKET_API_BASE_URL + '/' + ticketId + '/change-status?newStatus=declined');
    }

    changeTicketStatusToInProgress(ticketId) {
        return axios.put(TICKET_API_BASE_URL + '/' + ticketId + '/change-status?newStatus=in_progress');
    }

    changeTicketStatusToDone(ticketId) {
        return axios.put(TICKET_API_BASE_URL + '/' + ticketId + '/change-status?newStatus=done');
    }

    getNewTicketId() {
        return axios.get(TICKET_API_BASE_URL + '/next-ticket-id');
    }
}

export default new TicketService() 