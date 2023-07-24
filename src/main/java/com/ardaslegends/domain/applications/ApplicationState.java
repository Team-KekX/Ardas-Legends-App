package com.ardaslegends.domain.applications;

public enum ApplicationState {
    ACCEPTED("Accepted"),
    OPEN("Open"),
    DENIED_BY_STAFF("Denied by staff"),
    WITHDRAWN("Withdrawn");

    public final String displayName;
    ApplicationState(String displayName) {
        this.displayName = displayName;
    }
}
