package digit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.DTRConfiguration;
import digit.util.UserUtil;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {
    private UserUtil userUtils;

    private DTRConfiguration config;

    @Autowired
    public UserService(UserUtil userUtils, DTRConfiguration config) {
        this.userUtils = userUtils;
        this.config = config;
    }

    /**
     * Calls user service to enrich user from search or upsert user
     *
     * @param request
     */
    public void callUserService(DeathRegistrationRequest request) {
        request.getDeathRegistrationApplications().forEach(application -> {
            if (!StringUtils.isEmpty(application.getApplicant().getUuid())) {
                System.out.println("Applicant id found checking if this exist");
                try {
                    enrichUser(application, request.getRequestInfo());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Name of Applicant is : " + application.getApplicant().getName());
                User user = createFatherUser(application);
                try {
                    User userServiceRes = upsertUser(user, request.getRequestInfo());
                    application.getApplicant().setUuid(userServiceRes.getUuid());
                    application.getApplicant().setId(userServiceRes.getId());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private User createFatherUser(DeathRegistrationApplication application) {
        Applicant father = application.getApplicant();
        User user = User.builder().userName(father.getUserName())
                .mobileNumber(father.getMobileNumber())
                .emailId(father.getEmailId())
                .mobileNumber(father.getMobileNumber())
                .tenantId(father.getTenantId())
                .userType(father.getUserType())
                .roles(father.getRoles())
                .build();
        user.setName(application.getApplicant().getName());
        String tenantId = father.getTenantId();
        return user;
    }

    private User upsertUser(User user, RequestInfo requestInfo) throws JsonProcessingException {

        String tenantId = user.getTenantId();
        User userServiceResponse = null;

        // Search on mobile number as user name
        UserDetailResponse userDetailResponse = searchUser(userUtils.getStateLevelTenant(tenantId), null, user.getMobileNumber());
        if (!userDetailResponse.getUser().isEmpty()) {
            System.out.println("Something is found about the user");
            User userFromSearch = userDetailResponse.getUser().get(0);
            log.info(userFromSearch.toString());
            if (!user.getUserName().equalsIgnoreCase(userFromSearch.getUserName())) {
                userServiceResponse = userFromSearch;

                // FIXME : updateUser
//                userServiceResponse = updateUser(requestInfo,user,userFromSearch);
            } else userServiceResponse = userDetailResponse.getUser().get(0);
        } else {
            System.out.println("Couldn't found user so creating a one instead");
            userServiceResponse = createUser(requestInfo, tenantId, user);
        }

        // Enrich the accountId
        user.setUuid(userServiceResponse.getUuid());
        user.setId(userServiceResponse.getId());
        return userServiceResponse;
    }


    private void enrichUser(DeathRegistrationApplication application, RequestInfo requestInfo) throws JsonProcessingException {
        String accountIdFather = application.getApplicant().getUuid();
        String tenantId = application.getApplicant().getTenantId();

        UserDetailResponse userDetailResponseFather = searchUser(userUtils.getStateLevelTenant(tenantId), accountIdFather, application.getApplicant().getUserName());
        if (userDetailResponseFather.getUser().isEmpty())
            throw new CustomException("INVALID_ACCOUNTID", "No user exist for the given accountId");

        else application.setId(userDetailResponseFather.getUser().get(0).getUuid());

    }

    /**
     * Creates the user from the given userInfo by calling user service
     *
     * @param requestInfo
     * @param tenantId
     * @param userInfo
     * @return
     */
    private User createUser(RequestInfo requestInfo, String tenantId, User userInfo) {

        userUtils.addUserDefaultFields(userInfo.getMobileNumber(), tenantId, userInfo);
        StringBuilder uri = new StringBuilder(config.getUserHost())
                .append(config.getUserContextPath())
                .append(config.getUserCreateEndpoint());

        CreateUserRequest user = new CreateUserRequest(requestInfo, userInfo);
        System.out.println(user.toString());
        try {
            System.out.println(new ObjectMapper().writeValueAsString(user));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info(user.getUser().toString());
        UserDetailResponse userDetailResponse = userUtils.userCall(user, uri);

        return userDetailResponse.getUser().get(0);

    }

    /**
     * Updates the given user by calling user service
     *
     * @param requestInfo
     * @param user
     * @param userFromSearch
     * @return
     */
    private User updateUser(RequestInfo requestInfo, User user, User userFromSearch) {

        userFromSearch.setUserName(user.getUserName());
        userFromSearch.setActive(true);

        StringBuilder uri = new StringBuilder(config.getUserHost())
                .append(config.getUserContextPath())
                .append(config.getUserUpdateEndpoint());


        UserDetailResponse userDetailResponse = userUtils.userCall(new CreateUserRequest(requestInfo, userFromSearch), uri);

        return userDetailResponse.getUser().get(0);

    }

    /**
     * calls the user search API based on the given accountId and userName
     *
     * @param stateLevelTenant
     * @param accountId
     * @param userName
     * @return
     */
    public UserDetailResponse searchUser(String stateLevelTenant, String accountId, String userName) throws JsonProcessingException {

        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setActive(true);
        userSearchRequest.setUserType("CITIZEN");
        userSearchRequest.setTenantId(stateLevelTenant);

        if (StringUtils.isEmpty(accountId) && StringUtils.isEmpty(userName))
            return null;

        if (!StringUtils.isEmpty(accountId))
            userSearchRequest.setUuid(Collections.singletonList(accountId));

        if (!StringUtils.isEmpty(userName))
            userSearchRequest.setUserName(userName);

        StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
        System.out.println(new ObjectMapper().writeValueAsString(userSearchRequest));
        return userUtils.userCall(userSearchRequest, uri);

    }

    /**
     * calls the user search API based on the given list of user uuids
     *
     * @param uuids
     * @return
     */
    private Map<String, User> searchBulkUser(List<String> uuids) {

        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setActive(true);
        userSearchRequest.setUserType("CITIZEN");


        if (!CollectionUtils.isEmpty(uuids))
            userSearchRequest.setUuid(uuids);


        StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
        UserDetailResponse userDetailResponse = userUtils.userCall(userSearchRequest, uri);
        List<User> users = userDetailResponse.getUser();

        if (CollectionUtils.isEmpty(users))
            throw new CustomException("USER_NOT_FOUND", "No user found for the uuids");

        Map<String, User> idToUserMap = users.stream().collect(Collectors.toMap(User::getUuid, Function.identity()));

        return idToUserMap;
    }

}