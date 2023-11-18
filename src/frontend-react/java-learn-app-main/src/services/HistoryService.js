import axios from 'axios';

const TICKET_API_BASE_URL = "http://localhost:8081/tickets/";

class HistoryService {

    getLastFiveHistoryByTicketId(ticketId) {
        return axios.get(TICKET_API_BASE_URL + ticketId + "/history");
    }

    getAllHistoryByTicketId(ticketId) {
        return axios.get(TICKET_API_BASE_URL + ticketId + "/history?buttonValue=Show All");
    }
}

export default new HistoryService() 