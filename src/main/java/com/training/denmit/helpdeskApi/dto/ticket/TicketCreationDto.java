package com.training.denmit.helpdeskApi.dto.ticket;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.training.denmit.helpdeskApi.model.enums.Category;
import com.training.denmit.helpdeskApi.model.enums.Urgency;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
public class TicketCreationDto {

    @NotBlank(message = "Name must not be blank")
    @Size(min = 4, max = 100, message = "Name must be between 4 and 100")
    @Pattern(regexp = "^[^A-ZА-Яа-я]*$", message = "Name is not valid")
    private String name;

    @Size(max = 500, message = "Description must not be more then 500")
    @Pattern(regexp = "^[^А-Яа-я]*$", message = "Description is not valid")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "You can't select a date that is less than the current date")
    private LocalDate desiredResolutionDate;

    private Category category;

    private Urgency urgency;
}
