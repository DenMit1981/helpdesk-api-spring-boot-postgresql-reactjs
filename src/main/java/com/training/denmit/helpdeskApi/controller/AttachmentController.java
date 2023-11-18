package com.training.denmit.helpdeskApi.controller;

import com.training.denmit.helpdeskApi.dto.attachment.AttachmentDto;
import com.training.denmit.helpdeskApi.service.AttachmentService;
import com.training.denmit.helpdeskApi.service.ValidationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/tickets/{ticketId}/attachments")
@Api("Attachment controller")
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final ValidationService validationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "Upload new file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @PathVariable("ticketId") Long ticketId) throws IOException {
        List<String> fileUploadErrors = validationService.validateUploadFile(file);

        if (checkErrors(fileUploadErrors)) {
            return new ResponseEntity<>(fileUploadErrors, HttpStatus.BAD_REQUEST);
        }

        AttachmentDto attachmentDto = attachmentService.getChosenAttachment(file);

        attachmentService.save(attachmentDto, ticketId);

        return new ResponseEntity<>(attachmentService.getAllByTicketId(ticketId), HttpStatus.OK);
    }

    @GetMapping("/{attachmentId}")
    @ApiOperation(value = "Get file by ID")
    public ResponseEntity<AttachmentDto> getById(@PathVariable("attachmentId") Long attachmentId,
                                                 @PathVariable("ticketId") Long ticketId,
                                                 HttpServletResponse response) throws IOException {
        AttachmentDto attachmentDto = attachmentService.getById(attachmentId, ticketId);

        response.setContentType("application/octet-stream");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename = " + attachmentDto.getName();

        response.setHeader(headerKey, headerValue);

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(attachmentDto.getFile());
        }

        return ResponseEntity.ok(attachmentDto);
    }

    @GetMapping
    @ApiOperation(value = "Get all files by ticket ID")
    public ResponseEntity<List<AttachmentDto>> getAllByTicketId(@PathVariable("ticketId") Long ticketId) {
        return ResponseEntity.ok(attachmentService.getAllByTicketId(ticketId));
    }

    @DeleteMapping("/{attachmentName}")
    @ApiOperation(value = "Delete file by name")
    public ResponseEntity<?> deleteFile(@PathVariable("attachmentName") String attachmentName,
                                        @PathVariable("ticketId") Long ticketId) {
        attachmentService.deleteByName(attachmentName, ticketId);

        return new ResponseEntity<>(attachmentService.getAllByTicketId(ticketId), HttpStatus.OK);
    }

    private boolean checkErrors(List<String> fileUploadErrors) {
        return !fileUploadErrors.isEmpty();
    }
}
