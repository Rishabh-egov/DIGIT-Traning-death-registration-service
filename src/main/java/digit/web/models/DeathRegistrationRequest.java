package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Contract class to receive request. Array of  items are used in case of create, whereas single  item is used for update
 */
@ApiModel(description = "Contract class to receive request. Array of  items are used in case of create, whereas single  item is used for update")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-07-27T14:23:19.725+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeathRegistrationRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo = null;

//      @JsonProperty("RequestInfo")
//      private RequestInfo requestInfo = null;

//        @JsonProperty("userInfo")
//        private User UserInfo = null;

    @JsonProperty("DeathRegistrationApplications")
    @Valid
    private List<DeathRegistrationApplication> deathRegistrationApplications = null;


    public DeathRegistrationRequest addDeathRegistrationApplicationsItem(DeathRegistrationApplication deathRegistrationApplicationsItem) {
        if (this.deathRegistrationApplications == null) {
            this.deathRegistrationApplications = new ArrayList<>();
        }
        this.deathRegistrationApplications.add(deathRegistrationApplicationsItem);
        return this;
    }

}

