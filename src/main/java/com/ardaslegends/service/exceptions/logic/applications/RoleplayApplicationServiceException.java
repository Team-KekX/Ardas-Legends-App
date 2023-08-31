package com.ardaslegends.service.exceptions.logic.applications;

import com.ardaslegends.service.exceptions.ServiceException;

public class RoleplayApplicationServiceException extends ServiceException {

    private static final String NO_APPLICATION_FOUND_WITH_ID = "No roleplay application was found with id. [%s]";
    private static final String PLAYER_IS_NOT_STAFF = "Player [%s] is not staff and therefore cannot vote on applications";
    private static final String PLAYER_ALREADY_VOTED = "Player [%s] already added their vote to the application";
    private static final String PLAYER_DID_NOT_VOTE = "Player [%s] did not vote on the application and therefore cannot remove a vote";
    private static final String URL_NOT_VALID = "The given URL (%s) is not a valid URL!";
    private static final String APPLICATION_NOT_YET_ACCEPTED = "The application [%s] has not been accepted yet!";
    public static RoleplayApplicationServiceException noApplicationFoundWithId(Long id) { return new RoleplayApplicationServiceException(NO_APPLICATION_FOUND_WITH_ID.formatted(id)); }
    public static RoleplayApplicationServiceException playerIsNotStaff(String ign) { return new RoleplayApplicationServiceException(PLAYER_IS_NOT_STAFF.formatted(ign)); }
    public static RoleplayApplicationServiceException playerAlreadyVoted(String ign) { return new RoleplayApplicationServiceException(PLAYER_ALREADY_VOTED.formatted(ign)); }
    public static RoleplayApplicationServiceException playerDidNotVote(String ign) { return new RoleplayApplicationServiceException(PLAYER_DID_NOT_VOTE.formatted(ign)); }
    public static RoleplayApplicationServiceException urlIsNotValid(String url) { return new RoleplayApplicationServiceException(URL_NOT_VALID.formatted(url)); }
    public static RoleplayApplicationServiceException applicationNotYetAccepted(Long id) {return new RoleplayApplicationServiceException(APPLICATION_NOT_YET_ACCEPTED.formatted(id));}

    protected RoleplayApplicationServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected RoleplayApplicationServiceException(String message) {
        super(message);
    }
}
