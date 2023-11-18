package com.training.denmit.helpdeskApi.util.comparator;

import com.training.denmit.helpdeskApi.model.enums.Status;

import java.util.Comparator;

public class StatusComparator implements Comparator<Status> {

    @Override
    public int compare(Status o1, Status o2) {
        return o1.toString().compareTo(o2.toString());
    }
}
