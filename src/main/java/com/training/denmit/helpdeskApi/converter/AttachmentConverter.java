package com.training.denmit.helpdeskApi.converter;

import com.training.denmit.helpdeskApi.dto.attachment.AttachmentDto;
import com.training.denmit.helpdeskApi.model.Attachment;

public interface AttachmentConverter {

    AttachmentDto convertToAttachmentDto(Attachment attachment);

    Attachment fromAttachmentDto(AttachmentDto attachmentDto);
}
