package com.training.denmit.helpdeskApi.mail.service;

import com.training.denmit.helpdeskApi.model.enums.Status;

public interface EmailGenerateService {

    void generateEmail(Long ticketId, Status previousStatus, Status newStatus);
}
