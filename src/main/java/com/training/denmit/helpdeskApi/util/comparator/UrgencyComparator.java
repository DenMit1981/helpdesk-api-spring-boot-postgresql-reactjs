package com.training.denmit.helpdeskApi.util.comparator;

import com.training.denmit.helpdeskApi.exception.WrongSortOrderException;
import com.training.denmit.helpdeskApi.model.enums.Urgency;

import java.util.*;

public class UrgencyComparator implements Comparator<Urgency> {

    private static final Map<String, Integer> URGENCY_MAP = Map.of(
            "CRITICAL", 1,
            "HIGH", 2,
            "AVERAGE", 3,
            "LOW", 4
    );

    public int getOrderOfUrgency(Urgency urgency) {
        return URGENCY_MAP.entrySet().stream()
                .filter(pair -> pair.getKey().equals(urgency.name()))
                .map(Map.Entry::getValue)
                .mapToInt(Integer::intValue)
                .findFirst()
                .orElseThrow(() -> new WrongSortOrderException("Wrong order"));
    }

    @Override
    public int compare(Urgency o1, Urgency o2) {
        int urgency1 = getOrderOfUrgency(o1);
        int urgency2 = getOrderOfUrgency(o2);

        return urgency1 - urgency2;
    }
}
