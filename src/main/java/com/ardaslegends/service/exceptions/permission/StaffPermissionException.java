package com.ardaslegends.service.exceptions.permission;

public class StaffPermissionException extends PermissionException{

    private static final String NO_STAFF_PERMISSION = "Only staff members can perform this action!";
    protected StaffPermissionException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected StaffPermissionException(String message) {
        super(message);
    }

    public static StaffPermissionException noStaffPermission() {return new StaffPermissionException(NO_STAFF_PERMISSION);}
}
