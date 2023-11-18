package com.training.denmit.helpdeskApi.mail.enums;

import com.training.denmit.helpdeskApi.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

import static com.training.denmit.helpdeskApi.model.enums.Status.*;

@Getter
@AllArgsConstructor
public enum ChangeStatusMail {
    DRAFT_NEW(Map.of(DRAFT, NEW)),
    DECLINED_NEW(Map.of(DECLINED, NEW)),
    NEW_APPROVED(Map.of(NEW, APPROVED)),
    NEW_DECLINED(Map.of(NEW, DECLINED)),
    NEW_CANCELED(Map.of(NEW, CANCELED)),
    APPROVED_CANCELED(Map.of(APPROVED, CANCELED)),
    IN_PROGRESS_DONE(Map.of(IN_PROGRESS, DONE));

    private final Map<Status, Status> previousStatusToCurrentStatusMap;

    public boolean isPreviousStatusToCurrentStatusMapEqualTo(Map<Status, Status> other) {
        return previousStatusToCurrentStatusMap.equals(other);
    }
}

