import axios from 'axios';

const TICKET_API_BASE_URL = "http://localhost:8081/tickets/";

class AttachmentService {

    getAllAttachmentsByTicketId(ticketId) {
        return axios.get(TICKET_API_BASE_URL + ticketId + "/attachments");
    }

    uploadAttachment(attachment, ticketId) {
        return axios.post(TICKET_API_BASE_URL + ticketId + "/attachments", attachment);
    }

    getAttachmentById(attachmentId, ticketId) {
        return axios.get(TICKET_API_BASE_URL + ticketId + "/attachments/" + attachmentId, {
            responseType: "blob"
        });
    }

    deleteAttachment(attachmentName, ticketId) {
        return axios.delete(TICKET_API_BASE_URL + ticketId + "/attachments/" + attachmentName);
    }
}

export default new AttachmentService() 