package digit.service;


//import digit.enrichment.DeathApplicationEnrichment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.DTRConfiguration;
import digit.enrichment.DeathApplicationEnrichment;
import digit.kafka.Producer;
import digit.repository.DeathRegistrationRepository;
import digit.validators.DeathApplicationValidator;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DeathRegistrationService {

    @Autowired
    private DeathApplicationValidator validator;

    @Autowired
    private DeathApplicationEnrichment enrichmentUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private DeathRegistrationRepository DeathRegistrationRepository;

    @Autowired
    private Producer producer;
    private DTRConfiguration dtrConfiguration;

    public List<DeathRegistrationApplication> registerDtRequest(DeathRegistrationRequest deathRegistrationRequest) {
        // Validate applications
        validator.validateDeathApplication(deathRegistrationRequest);

        // Enrich applications
        enrichmentUtil.enrichDeathApplication(deathRegistrationRequest);

        // Enrich/Upsert user in upon Death registration
        userService.callUserService(deathRegistrationRequest);

        // Initiate workflow for the new application
        deathRegistrationRequest = workflowService.updateWorkflowStatus(deathRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
        producer.push(dtrConfiguration.getDtrCreateTopic(), deathRegistrationRequest);

        // Return the response back to user
        return deathRegistrationRequest.getDeathRegistrationApplications();
    }

    public List<DeathRegistrationApplication> searchDtApplications(RequestInfo requestInfo, DeathApplicationSearchCriteria DeathApplicationSearchCriteria) {
        // Fetch applications from database according to the given search criteria
        List<DeathRegistrationApplication> applications = DeathRegistrationRepository.getApplications(DeathApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if (CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        // Enrich applicant objects
        applications.forEach(application -> {
            ProcessInstance obj = workflowService.getCurrentWorkflow(requestInfo, application.getTenantId(), application.getApplicationNumber());
            try {
                System.out.println(new ObjectMapper().writeValueAsString(application));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            application.setWorkflow(Workflow.builder().status(obj.getState().getState()).action(obj.getAction()).documents(obj.getDocuments()).comments(obj.getComment()).build());

        });

        // Otherwise return the found applications
        return applications;
    }

    public DeathRegistrationApplication updateBtApplication(DeathRegistrationRequest DeathRegistrationRequest) {

        // Validate whether the application that is being requested for update indeed exists

        DeathRegistrationApplication existingApplication = validator.validateApplicationExistence(DeathRegistrationRequest.getDeathRegistrationApplications().get(0));
        existingApplication.setWorkflow(DeathRegistrationRequest.getDeathRegistrationApplications().get(0).getWorkflow());
        DeathRegistrationRequest.setDeathRegistrationApplications(Collections.singletonList(existingApplication));

        // Enrich application upon update
        enrichmentUtil.enrichDeathApplicationUponUpdate(DeathRegistrationRequest);

        if(DeathRegistrationRequest.getDeathRegistrationApplications().get(0).getWorkflow() == null)
        {
            DeathRegistrationRequest.getDeathRegistrationApplications().get(0).setDeceasedFirstName(DeathRegistrationRequest.getDeathRegistrationApplications().get(0).getDeceasedFirstName());
            DeathRegistrationRequest.getDeathRegistrationApplications().get(0).setTimeOfDeath(DeathRegistrationRequest.getDeathRegistrationApplications().get(0).getTimeOfDeath());
            producer.push(dtrConfiguration.getDtrUpdateTopic(), DeathRegistrationRequest);
            return DeathRegistrationRequest.getDeathRegistrationApplications().get(0);
        }

        workflowService.updateWorkflowStatus(DeathRegistrationRequest);

        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(dtrConfiguration.getDtrUpdateTopic(), DeathRegistrationRequest);

        return DeathRegistrationRequest.getDeathRegistrationApplications().get(0);
    }
}
