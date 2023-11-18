import axios from 'axios';

const TICKET_API_BASE_URL = "http://localhost:8081/tickets/";

class CommentService {

    createComment(comment, ticketId) {
        return axios.post(TICKET_API_BASE_URL + ticketId + "/comments", comment);
    }

    getLastFiveCommentsByTicketId(ticketId) {
        return axios.get(TICKET_API_BASE_URL + ticketId + "/comments");
    }

    getAllCommentsByTicketId(ticketId) {
        return axios.get(TICKET_API_BASE_URL + ticketId + "/comments?buttonValue=Show All");
    }
}

export default new CommentService() 