package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.validation.annotation.Validated;

/**
 * This object holds list of documents attached during the transaciton for a property
 */
@ApiModel(description = "This object holds list of documents attached during the transaciton for a property")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-07-27T14:23:19.725+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Document {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("documentType")
    private String documentType = null;

    @JsonProperty("fileStoreId")
    private String fileStore = null;

    @JsonProperty("documentUid")
    private String documentUid = null;

    @JsonProperty("additionalDetails")
    private Object additionalDetails = null;


}

