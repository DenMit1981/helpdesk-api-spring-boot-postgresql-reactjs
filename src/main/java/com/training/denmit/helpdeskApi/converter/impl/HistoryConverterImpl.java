package com.training.denmit.helpdeskApi.converter.impl;

import com.training.denmit.helpdeskApi.converter.HistoryConverter;
import com.training.denmit.helpdeskApi.dto.history.HistoryDto;
import com.training.denmit.helpdeskApi.model.History;
import org.springframework.stereotype.Component;

@Component
public class HistoryConverterImpl implements HistoryConverter {

    @Override
    public HistoryDto convertToHistoryDto(History history) {
        HistoryDto historyDto = new HistoryDto();

        historyDto.setDate(history.getDate());
        historyDto.setUser(history.getUser().getLastName() + " " + history.getUser().getFirstName());
        historyDto.setAction(history.getAction());
        historyDto.setDescription(history.getDescription());

        return historyDto;
    }
}
