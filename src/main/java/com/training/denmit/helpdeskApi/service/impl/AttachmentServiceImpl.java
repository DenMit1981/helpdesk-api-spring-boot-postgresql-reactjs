package com.training.denmit.helpdeskApi.service.impl;

import com.training.denmit.helpdeskApi.converter.AttachmentConverter;
import com.training.denmit.helpdeskApi.dto.attachment.AttachmentDto;
import com.training.denmit.helpdeskApi.exception.AttachmentIsPresentException;
import com.training.denmit.helpdeskApi.exception.AttachmentNotFoundException;
import com.training.denmit.helpdeskApi.model.Attachment;
import com.training.denmit.helpdeskApi.model.Ticket;
import com.training.denmit.helpdeskApi.repository.AttachmentRepository;
import com.training.denmit.helpdeskApi.service.AttachmentService;
import com.training.denmit.helpdeskApi.service.HistoryService;
import com.training.denmit.helpdeskApi.service.TicketService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private static final Logger LOGGER = LogManager.getLogger(AttachmentServiceImpl.class.getName());

    private static final String FILE_NOT_FOUND = "The file not found";
    private static final String FILE_IS_PRESENT = "The file is already downloaded";

    private final AttachmentRepository attachmentRepository;
    private final AttachmentConverter attachmentConverter;
    private final HistoryService historyService;
    private final TicketService ticketService;

    @Override
    @Transactional
    public Attachment save(@NonNull AttachmentDto attachmentDto, Long ticketId) {
        Attachment attachment = attachmentConverter.fromAttachmentDto(attachmentDto);

        Ticket ticket = ticketService.findById(ticketId);

        attachment.setTicket(ticket);

        if (!isFilePresent(attachment.getName(), ticketId)) {

            attachmentRepository.save(attachment);

            historyService.saveHistoryForAttachedFile(attachment, ticket);


            LOGGER.info("New file {} has just been added to ticket {}", attachmentDto.getName(), ticketId);

            return attachment;
        } else {
            LOGGER.error(FILE_IS_PRESENT);

            throw new AttachmentIsPresentException(FILE_IS_PRESENT);
        }
    }

    @Override
    @Transactional
    public AttachmentDto getById(Long attachmentId, Long ticketId) {
        Attachment attachment = attachmentRepository.findByIdAndTicketId(attachmentId, ticketId)
                .orElseThrow(() -> new AttachmentNotFoundException(FILE_NOT_FOUND));

        LOGGER.info("File {} is downloaded", attachment.getName());

        return attachmentConverter.convertToAttachmentDto(attachment);
    }

    @Override
    @Transactional
    public List<AttachmentDto> getAllByTicketId(Long ticketId) {
        List<Attachment> attachments = attachmentRepository.findAllByTicketId(ticketId);

        LOGGER.info("All files for ticket {} : {}", ticketId, attachments);

        return attachments.stream()
                .map(attachmentConverter::convertToAttachmentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByName(String fileName, Long ticketId) {
        if (isFilePresent(fileName, ticketId)) {

            historyService.saveHistoryForRemovedFile(fileName, ticketService.findById(ticketId));

            attachmentRepository.deleteByNameAndTicketId(fileName, ticketId);

            LOGGER.info("File {} has just been deleted from ticket {}", fileName, ticketId);
        } else {
            LOGGER.error(FILE_NOT_FOUND);

            throw new AttachmentNotFoundException(FILE_NOT_FOUND);
        }
    }

    @Override
    public AttachmentDto getChosenAttachment(@NonNull MultipartFile file) throws IOException {
        AttachmentDto attachmentDto = new AttachmentDto();

        attachmentDto.setName(file.getOriginalFilename());
        attachmentDto.setFile(file.getBytes());

        return attachmentDto;
    }

    private boolean isFilePresent(String fileName, Long ticketId) {
        return attachmentRepository.findAllByTicketId(ticketId).stream()
                .anyMatch(file -> fileName.equals(file.getName()));
    }
}
