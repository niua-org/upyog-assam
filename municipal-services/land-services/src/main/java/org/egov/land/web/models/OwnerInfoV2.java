package org.egov.land.web.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OwnerInfo
 */
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OwnerInfoV2 {

    private Long id;

    /**
     * User uuid from user service
     */
    private String uuid;

    /**
     * Tenant ID of the owner
     */
    @SafeHtml
    @JsonProperty("tenantId")
    private String tenantId = null;

    /**
     * Name of the owner
     */
    @SafeHtml
    @JsonProperty("name")
    private String name = null;

    /**
     * Mobile number of the owner
     */
    @SafeHtml
    @JsonProperty("mobileNumber")
    private String mobileNumber = null;

    /**
     * Alternate contact number of the owner
     */
    @Size(max = 50)
    @SafeHtml
    @JsonProperty("altContactNumber")
    private String altContactNumber;

    /**
     * Gender of the owner
     */
    @SafeHtml
    private String gender;
    /**
     * Relationship details of the owner
     */
    @JsonProperty("relationship")
    private Relationship relationship = null;

    private String dob;

    /**
     * Father's or husband's name of the owner
     */
    @SafeHtml
    @JsonProperty("fatherOrHusbandName")
    private String fatherOrHusbandName = null;

    /**
     * Mother's name of the owner
     */
    @SafeHtml
    private String motherName = null;

    /**
     * Email ID of the owner
     */
    @Size(max = 128)
    @SafeHtml
    @JsonProperty("emailId")
    private String emailId;

    /**
     * PAN number of the owner
     */
    @Size(max = 10)
    @SafeHtml
    private String panNumber;

    /**
     * Aadhaar number of the owner
     */
  //  @Pattern(regexp = "^[0-9]{12}$", message = "AdharNumber should be 12 digit number")
    @SafeHtml
    @JsonProperty("aadhaarNumber")
    private String aadhaarNumber;

    /**
     * Permanent address of the owner
     */
    @JsonProperty("permanentAddress")
    private Address permanentAddress;

    /**
     * Correspondence address of the owner
     */
    @JsonProperty("correspondenceAddress")
    private Address correspondenceAddress = null;

    /**
     * Indicates if the owner is the primary owner
     */
    @JsonProperty("isPrimaryOwner")
    private Boolean isPrimaryOwner = null;

    /**
     * Ownership percentage of the owner
     */
    @JsonProperty("ownerShipPercentage")
    private BigDecimal ownerShipPercentage = null;

    /**
     * Type of the owner
     */
    @SafeHtml
    @JsonProperty("ownerType")
    private String ownerType = null;

    /**
     * Status of the owner
     */
    @JsonProperty("status")
    private Boolean status = null;

    /**
     * Institution ID associated with the owner
     */
    @SafeHtml
    @JsonProperty("institutionId")
    private String institutionId = null;

    /**
     * List of documents associated with the owner
     */
    @JsonProperty("documents")
    @Valid
    private List<Document> documents = null;

    /**
     * Additional details about the owner
     */
    @JsonProperty("additionalDetails")
    private Object additionalDetails = null;

    /**
     * Created by user ID
     */
    @Size(max = 64)
    @JsonProperty("createdBy")
    private String createdBy;

    /**
     * Creation timestamp
     */
    @JsonProperty("createdDate")
    private Long createdDate;

    /**
     * Last modified by user ID
     */
    @Size(max = 64)
    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;

    /**
     * Last modification timestamp
     */
    @JsonProperty("lastModifiedDate")
    private Long lastModifiedDate;

    /**
     * Audit details of the owner
     */
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;


    public boolean compareWithExistingUser(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OwnerInfoV2 ownerInfo = (OwnerInfoV2) o;
        return Objects.equals(name, ownerInfo.name) &&
                Objects.equals(mobileNumber, ownerInfo.mobileNumber) &&
                Objects.equals(gender, ownerInfo.gender) &&
                Objects.equals(emailId, ownerInfo.emailId) &&
//              Objects.equals(dob, user.dob) && //Epoch format not converting properly from UI
                Objects.equals(fatherOrHusbandName, ownerInfo.fatherOrHusbandName) &&
                Objects.equals(correspondenceAddress, ownerInfo.correspondenceAddress);
    }

    /*
     * Populates Owner fields from the given User object
     *
     * @param user
     *            User object obtained from user service
     */
    public void addUserWithoutAuditDetail(OwnerInfo user) {
        this.setUuid(user.getUuid());
        this.setId(user.getId());
     //   this.setUserName(user.getUserName());
        //	this.setPassword(user.getPassword());
        //	this.setSalutation(user.getSalutation());
        this.setName(user.getName());
        this.setGender(user.getGender());
        this.setMobileNumber(user.getMobileNumber());
        this.setEmailId(user.getEmailId());
        this.setAltContactNumber(user.getAltContactNumber());
        this.setPanNumber(user.getPan());
        this.setAadhaarNumber(user.getAadhaarNumber());
        //this.setPermanentAddress(user.getPermanentAddress());
        //	this.setPermanentCity(user.getPermanentCity());
        //	this.setPermanentPincode(user.getPermanentPincode());
        //	this.setCorrespondenceAddress(user.getCorrespondenceAddress());
        //	this.setCorrespondenceCity(user.getCorrespondenceCity());
        //	this.setCorrespondencePincode(user.getCorrespondencePincode());
   //     this.setActive(user.getActive());
        //this.setDob(user.getDob());
        //	this.setPwdExpiryDate(user.getPwdExpiryDate());
        //	this.setLocale(user.getLocale());
    //    this.setType(user.getType());
        //this.setAccountLocked(user.getAccountLocked());
        //	this.setRoles(user.getRoles());
        this.setFatherOrHusbandName(user.getFatherOrHusbandName());
        //	this.setBloodGroup(user.getBloodGroup());
        //	this.setIdentificationMark(user.getIdentificationMark());
        //	this.setPhoto(user.getPhoto());
        this.setTenantId(user.getTenantId());
    }
}