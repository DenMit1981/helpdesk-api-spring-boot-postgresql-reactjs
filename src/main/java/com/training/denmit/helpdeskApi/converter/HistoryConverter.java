package com.training.denmit.helpdeskApi.converter;

import com.training.denmit.helpdeskApi.dto.history.HistoryDto;
import com.training.denmit.helpdeskApi.model.History;

public interface HistoryConverter {

    HistoryDto convertToHistoryDto(History history);
}
