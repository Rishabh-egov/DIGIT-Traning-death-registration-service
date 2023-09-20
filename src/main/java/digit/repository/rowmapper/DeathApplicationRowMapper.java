package digit.repository.rowmapper;

import digit.web.models.Address;
import digit.web.models.Applicant;
import digit.web.models.AuditDetails;
import digit.web.models.DeathRegistrationApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class DeathApplicationRowMapper implements ResultSetExtractor<List<DeathRegistrationApplication>> {
    public List<DeathRegistrationApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, DeathRegistrationApplication> deathRegistrationApplicationMap = new LinkedHashMap<>();

        while (rs.next()) {
            String uuid = rs.getString("bapplicationnumber");
            DeathRegistrationApplication deathRegistrationApplication = deathRegistrationApplicationMap.get(uuid);

            if (deathRegistrationApplication == null) {

                Long lastModifiedTime = rs.getLong("blastModifiedTime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }


                Applicant father = Applicant.builder().id(rs.getInt("bfatherid")).build();
                Applicant mother = Applicant.builder().id(rs.getInt("bmotherid")).build();

                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("bcreatedBy"))
                        .createdTime(rs.getLong("bcreatedTime"))
                        .lastModifiedBy(rs.getString("blastModifiedBy"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();

                deathRegistrationApplication = DeathRegistrationApplication.builder()
                        .applicationNumber(rs.getString("bapplicationnumber"))
                        .tenantId(rs.getString("btenantid"))
                        .id(rs.getString("bid"))
                        .deceasedFirstName(rs.getString("bdeceasedFirstName"))
                        .deceasedLastName(rs.getString("bdeceasedLastName"))
                        .placeOfDeath(rs.getString("bplaceOfDeath"))
                        .timeOfDeath(rs.getInt("btimeOfDeath"))
                        .auditDetails(auditdetails)
                        .build();
            }
            addChildrenToProperty(rs, deathRegistrationApplication);
            deathRegistrationApplicationMap.put(uuid, deathRegistrationApplication);
        }
        return new ArrayList<>(deathRegistrationApplicationMap.values());
    }

    private void addChildrenToProperty(ResultSet rs, DeathRegistrationApplication deathRegistrationApplication)
            throws SQLException {
        addAddressToApplication(rs, deathRegistrationApplication);
    }

    private void addAddressToApplication(ResultSet rs, DeathRegistrationApplication deathRegistrationApplication) throws SQLException {
        Address address = Address.builder()
                .registrationId(rs.getString("aid"))
                .tenantId(rs.getString("atenantid"))
                .latitude(rs.getDouble("alatitude"))
                .longitude(rs.getDouble("alongitude"))
                .addressId(rs.getString("aaddressid"))
                .addressNumber(rs.getString("aaddressnumber"))
                .addressLine1(rs.getString("aaddressline1"))
                .addressLine2(rs.getString("aaddressline2"))
                .landmark(rs.getString("alandmark"))
                .city(rs.getString("acity"))
                .pincode(rs.getString("apincode"))
                .detail("adetail")
                .registrationId("aregistrationid")
                .build();

        deathRegistrationApplication.setAddressOfDeceased(address);

    }

}
