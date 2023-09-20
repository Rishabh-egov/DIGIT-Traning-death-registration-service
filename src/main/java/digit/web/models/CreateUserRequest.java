package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CreateUserRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("user")
    private User user;

    @Override
    public String toString() {
        return "CreateUserRequest{" +
                "requestInfo=" + requestInfo +
                ", user=" + user.toString() +
                '}';
    }
}