package com.training.denmit.helpdeskApi.dto.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttachmentDto {

    private Long id;

    private String name;

    @JsonIgnore
    private byte[] file;
}
