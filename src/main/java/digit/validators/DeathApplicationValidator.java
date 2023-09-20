package digit.validators;

import digit.repository.DeathRegistrationRepository;
import digit.web.models.DeathApplicationSearchCriteria;
import digit.web.models.DeathRegistrationApplication;
import digit.web.models.DeathRegistrationRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class DeathApplicationValidator {

    @Autowired
    private DeathRegistrationRepository repository;

    public void validateDeathApplication(DeathRegistrationRequest DeathRegistrationRequest) {
        DeathRegistrationRequest.getDeathRegistrationApplications().forEach(application -> {
            if (ObjectUtils.isEmpty(application.getTenantId()))
                throw new CustomException("EG_BT_APP_ERR", "tenantId is mandatory for creating Death registration applications");
        });
    }

    public DeathRegistrationApplication validateApplicationExistence(DeathRegistrationApplication DeathRegistrationApplication) {
        return repository.getApplications(DeathApplicationSearchCriteria.builder().applicationNumber(DeathRegistrationApplication.getApplicationNumber()).build()).get(0);
    }
}