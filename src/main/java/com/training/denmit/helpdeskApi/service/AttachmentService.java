package com.training.denmit.helpdeskApi.service;

import com.training.denmit.helpdeskApi.dto.attachment.AttachmentDto;
import com.training.denmit.helpdeskApi.model.Attachment;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AttachmentService {

    Attachment save(@NonNull AttachmentDto attachmentDto, Long ticketId);

    AttachmentDto getById(Long attachmentId, Long ticketId);

    List<AttachmentDto> getAllByTicketId(Long ticketId);

    void deleteByName(String name, Long ticketId);

    AttachmentDto getChosenAttachment(MultipartFile file) throws IOException;
}
