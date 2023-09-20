package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * This is acting ID token of the authenticated user on the server. Any value provided by the clients will be ignored and actual user based on authtoken will be used on the server.
 */
@ApiModel(description = "This is acting ID token of the authenticated user on the server. Any value provided by the clients will be ignored and actual user based on authtoken will be used on the server.")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-07-27T14:23:19.725+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class    User {
    @JsonProperty("tenantId")
    private String tenantId = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("id")
    private Integer id = null;

    @JsonProperty("uuid")
    private String uuid = null;

    @JsonProperty("userName")
    private String userName = null;

    @JsonProperty("type")
    private String userType = null;

    @JsonProperty("gender")
    private String gender = null;

    @JsonProperty("password")
    private String password = null;

    @JsonProperty("salutation")
    private String salutation = null;

    @JsonProperty("pan")
    private String pan = null;

    @JsonProperty("aadhaarNumber")
    private String aadhaarNumber = null;

    @JsonProperty("mobileNumber")
    private String mobileNumber = null;

    @JsonProperty("emailId")
    private String emailId = null;

    @JsonProperty("roles")
    @Valid
    private List<Role> roles = new ArrayList<>();

    @JsonProperty("permanentAddress")
    private String permanentAddress = null;

    @JsonProperty("permanentCity")
    private String permanentCity = null;

    @JsonProperty("permanentPincode")
    private String permanentPincode = null;

    @JsonProperty("correspondencePincode")
    private String correspondencePincode = null;

    @JsonProperty("active")
    private Boolean active = null;

    @JsonProperty("locale")
    private String locale = null;

    @JsonProperty("signature")
    private String signature = null;

    @JsonProperty("accountLocked")
    private Boolean accountLocked = null;

    @JsonProperty("fatherOrSpouseName")
    private String fatherOrSpouseName = null;

    @JsonProperty("bloodGroup")
    private String bloodGroup = null;

    @JsonProperty("identificationMark")
    private String identificationMark = null;

    @JsonProperty("photo")
    private String photo = null;


    public User addRolesItem(Role rolesItem) {
        this.roles.add(rolesItem);
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "tenantId='" + tenantId + '\'' +
                ", id=" + id +
                ", uuid='" + uuid + '\'' +
                ", userName='" + userName + '\'' +
                ", userType='" + userType + '\'' +
                ", gender='" + gender + '\'' +
                ", password='" + password + '\'' +
                ", salutation='" + salutation + '\'' +
                ", pan='" + pan + '\'' +
                ", aadhaarNumber='" + aadhaarNumber + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", emailId='" + emailId + '\'' +
                ", roles=" + roles +
                ", permanentAddress='" + permanentAddress + '\'' +
                ", permanentCity='" + permanentCity + '\'' +
                ", permanentPincode='" + permanentPincode + '\'' +
                ", correspondencePincode='" + correspondencePincode + '\'' +
                ", active=" + active +
                ", locale='" + locale + '\'' +
                ", signature='" + signature + '\'' +
                ", accountLocked=" + accountLocked +
                ", fatherOrSpouseName='" + fatherOrSpouseName + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", identificationMark='" + identificationMark + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}

