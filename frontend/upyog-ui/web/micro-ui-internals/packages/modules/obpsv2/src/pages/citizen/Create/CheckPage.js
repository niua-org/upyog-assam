import React, { useState } from "react";
import {
  Card,
  CardHeader,
  CardSubHeader,
  StatusTable,
  Row,
  CheckBox,
  SubmitBar,
  LinkButton,
  EditIcon,
  CardLabel,
  Header,
  Table,
} from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { checkForNA, getOrderDocuments } from "../../../utils";
import DocumentsPreview from "../../../../../templates/ApplicationDetails/components/DocumentsPreview";

const ActionButton = ({ jumpTo }) => {
  const history = useHistory();

  function routeTo() {
    history.push(jumpTo);
  }

  return (
    <LinkButton
      label={
        <EditIcon
          style={{ marginTop: "-5px", float: "right", position: "relative" }}
        />
      }
      className="check-page-link-button"
      onClick={routeTo}
    />
  );
};

const CheckPage = ({ onSubmit, value = {} }) => {
  const { t } = useTranslation();
  const [agree, setAgree] = useState(false);
  const [isExpanded, setIsExpanded] = useState(false);
  const toggleExpanded = () => setIsExpanded((prev) => !prev);
  const { applicant = {}, address = {}, land = {}, documents = {} } = value;
  const setDeclarationHandler = () => {
    setAgree(!agree);
  };
  const cellStyle = {
    border: "1px solid #ccc",
    padding: "8px",
    textAlign: "left",
    fontSize: "14px",
  };
  
  const isEditApplication = window.location.href.includes("editApplication");

  let improvedDoc = [];
  
  documents?.documents?.map((appDoc) => {
    improvedDoc.push({ ...appDoc, module: "BPA" });
  });
  const {
    data: pdfDetails,
    isLoading: pdfLoading,
    error,
  } = Digit.Hooks.useDocumentSearch(improvedDoc, {
    enabled: improvedDoc?.length > 0 ? true : false,
  });

  let applicationDocs = [];
  if (pdfDetails?.pdfFiles?.length > 0) {
    pdfDetails?.pdfFiles?.map((pdfAppDoc) => {
      if (pdfAppDoc?.module == "BPA") applicationDocs.push(pdfAppDoc);
    });
  }

  const getDetailsRow = (formDetails) => {
    if (!formDetails) return null;

    const renderTable = (data, key) => {
      if (!Array.isArray(data) || data.length === 0) return null;

      const headers = Object.keys(data[0]);

      return (
        <div key={key} style={{ marginTop: "20px" }}>
          <h4>{t(key.toUpperCase())}</h4>
          <table
            style={{
              borderCollapse: "collapse",
              border: "1px solid #ccc",
              tableLayout: "auto",
              width: "100%",
              fontSize: "12px",
              lineHeight: "1.5",
            }}
          >
            <thead>
              <tr style={{ backgroundColor: "#f0f0f0" }}>
                <th style={cellStyle}>Sl. No.</th>
                {headers.map((header) => (
                  <th key={header} style={cellStyle}>
                    {t(header.toUpperCase())}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {data.map((row, idx) => (
                <tr key={idx}>
                  <td style={cellStyle}>{idx + 1}</td>
                  {headers.map((field) => (
                    <td key={field} style={cellStyle}>
                      {row[field] || "NA"}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      );
    };

    return (
      <div>
        <StatusTable>
          {Object.entries(formDetails).map(([key, value], index) => {
            if (typeof value === "string") {
              return (
                <Row
                  key={index}
                  label={t(key.toUpperCase())}
                  text={value?.trim() || "NA"}
                />
              );
            }

            if (Array.isArray(value)) {
              return renderTable(value, key);
            }

            return null;
          })}
        </StatusTable>
      </div>
    );
  };

  return (
    <React.Fragment>
      <Card>
        <CardHeader>{t("BPA_SUMMARY_PAGE")}</CardHeader>

        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <CardSubHeader style={{ fontSize: "24px", marginTop: "24px" }}>
            {t("BPA_APPLICANT_DETAILS")}
          </CardSubHeader>
          <ActionButton
            jumpTo={`/upyog-ui/citizen/obpsv2/building-permit/applicant-details`}
          />
        </div>

        <StatusTable>
          <Row
            label={t("BPA_APPLICANT_NAME")}
            text={checkForNA(applicant?.applicantName)}
          />
          <Row
            label={t("BPA_MOBILE_NO")}
            text={checkForNA(applicant?.mobileNumber)}
          />
          <Row
            label={t("BPA_ALT_MOBILE_NO")}
            text={checkForNA(applicant?.alternateNumber)}
          />
          <Row
            label={t("BPA_EMAIL_ID")}
            text={checkForNA(applicant?.emailId)}
          />
          <Row
            label={t("BPA_FATHER_NAME")}
            text={checkForNA(applicant?.fatherName)}
          />
          <Row
            label={t("BPA_MOTHER_NAME")}
            text={checkForNA(applicant?.motherName)}
          />
          <Row
            label={t("BPA_PAN_CARD")}
            text={checkForNA(applicant?.panCardNumber)}
          />
          <Row
            label={t("BPA_AADHAAR_CARD")}
            text={checkForNA(applicant?.aadhaarNumber)}
          />
        </StatusTable>

        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginTop: "24px",
          }}
        >
          <CardSubHeader style={{ fontSize: "24px" }}>
            {t("BPA_ADDRESS_DETAILS")}
          </CardSubHeader>
          <ActionButton
            jumpTo={`/upyog-ui/citizen/obpsv2/building-permit/address-details`}
          />
        </div>

        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginTop: "16px",
          }}
        >
          <CardSubHeader style={{ fontSize: "20px" }}>
            {t("BPA_PERMANENT_ADDRESS")}
          </CardSubHeader>
        </div>

        <StatusTable>
          <Row
            label={t("BPA_HOUSE_NO")}
            text={checkForNA(address?.permanent?.houseNo)}
          />
          <Row
            label={t("BPA_ADDRESS_LINE_1")}
            text={checkForNA(address?.permanent?.addressLine1)}
          />
          <Row
            label={t("BPA_ADDRESS_LINE_2")}
            text={checkForNA(address?.permanent?.addressLine2)}
          />
          <Row
            label={t("BPA_DISTRICT")}
            text={checkForNA(address?.permanent?.district?.name)}
          />
          <Row
            label={t("BPA_CITY_VILLAGE")}
            text={checkForNA(address?.permanent?.city?.name)}
          />
          <Row
            label={t("BPA_STATE")}
            text={checkForNA(address?.permanent?.state?.name)}
          />
          <Row
            label={t("BPA_PIN_CODE")}
            text={checkForNA(address?.permanent?.pincode)}
          />
        </StatusTable>

        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginTop: "16px",
          }}
        >
          <CardSubHeader style={{ fontSize: "20px" }}>
            {t("BPA_CORRESPONDENCE_ADDRESS")}
          </CardSubHeader>
        </div>

        {address?.sameAsPermanent ? (
          <div style={{ marginTop: "16px" }}>
            <CheckBox
              label={t("BPA_SAME_AS_PERMANENT")}
              checked={true}
              disabled={true}
            />
          </div>
        ) : (
          <StatusTable style={{ marginTop: "16px" }}>
            <Row
              label={t("BPA_HOUSE_NO")}
              text={checkForNA(address?.correspondence?.houseNo)}
            />
            <Row
              label={t("BPA_ADDRESS_LINE_1")}
              text={checkForNA(address?.correspondence?.addressLine1)}
            />
            <Row
              label={t("BPA_ADDRESS_LINE_2")}
              text={checkForNA(address?.correspondence?.addressLine2)}
            />
            <Row
              label={t("BPA_DISTRICT")}
              text={checkForNA(address?.correspondence?.district?.name)}
            />
            <Row
              label={t("BPA_CITY_VILLAGE")}
              text={checkForNA(address?.correspondence?.city?.name)}
            />
            <Row
              label={t("BPA_STATE")}
              text={checkForNA(address?.correspondence?.state?.name)}
            />
            <Row
              label={t("BPA_PIN_CODE")}
              text={checkForNA(address?.correspondence?.pincode)}
            />
          </StatusTable>
        )}

        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginTop: "24px",
          }}
        >
          <CardSubHeader style={{ fontSize: "24px" }}>
            {t("BPA_LAND_DETAILS")}
          </CardSubHeader>
          <ActionButton
            jumpTo={`/upyog-ui/citizen/obpsv2/building-permit/land-details`}
          />
        </div>

        <StatusTable>
          <Row
            label={t("BPA_CONSTRUCTION_TYPE")}
            text={checkForNA(land?.constructionType?.name)}
          />
          <Row
            label={t("BPA_OLD_DAG_NUMBER")}
            text={checkForNA(land?.oldDagNumber)}
          />
          <Row
            label={t("BPA_NEW_DAG_NUMBER")}
            text={checkForNA(land?.newDagNumber)}
          />
          <Row
            label={t("BPA_OLD_PATTA_NUMBER")}
            text={checkForNA(land?.oldPattaNumber)}
          />
          <Row
            label={t("BPA_NEW_PATTA_NUMBER")}
            text={checkForNA(land?.newPattaNumber)}
          />
          <Row
            label={t("BPA_TOTAL_PLOT_AREA")}
            text={land?.totalPlotArea ? `${land.totalPlotArea} sq. ft.` : ""}
          />
        </StatusTable>

        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginTop: "16px",
          }}
        >
          <CardSubHeader style={{ fontSize: "20px" }}>
            {t("BPA_ADJOINING_LAND_OWNERS")}
          </CardSubHeader>
        </div>

        <StatusTable>
          <Row
            label={t("BPA_NORTH")}
            text={checkForNA(land?.adjoiningOwners?.north)}
          />
          <Row
            label={t("BPA_SOUTH")}
            text={checkForNA(land?.adjoiningOwners?.south)}
          />
          <Row
            label={t("BPA_EAST")}
            text={checkForNA(land?.adjoiningOwners?.east)}
          />
          <Row
            label={t("BPA_WEST")}
            text={checkForNA(land?.adjoiningOwners?.west)}
          />
        </StatusTable>

        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginTop: "16px",
          }}
        >
          <CardSubHeader style={{ fontSize: "20px" }}>
            {t("BPA_FUTURE_PROVISIONS")}
          </CardSubHeader>
        </div>

        <StatusTable>
          <Row
            label={t("BPA_VERTICAL_EXTENSION")}
            text={checkForNA(land?.futureProvisions?.verticalExtension?.name)}
          />
          <Row
            label={t("BPA_HORIZONTAL_EXTENSION")}
            text={checkForNA(land?.futureProvisions?.horizontalExtension?.name)}
          />
        </StatusTable>

        <StatusTable style={{ marginTop: "16px" }}>
          <Row
            label={t("BPA_RTP_CATEGORY")}
            text={checkForNA(land?.rtpCategory?.name)}
          />
          <Row
            label={t("BPA_REGISTERED_TECHNICAL_PERSON")}
            text={checkForNA(land?.registeredTechnicalPerson?.name)}
          />
          <Row
            label={t("BPA_OCCUPANCY_TYPE")}
            text={checkForNA(land?.occupancyType?.name)}
          />
          <Row
            label={t("BPA_TOD_BENEFITS")}
            text={
              land?.todBenefits
                ? `${t("CS_YES")}, ${land.todBenefits}`
                : t("CS_NO")
            }
          />
          <Row
            label={t("BPA_FORM_36")}
            text={land?.form36 ? t("BPA_FILE_UPLOADED") : t("CS_NA")}
          />
          <Row
            label={t("BPA_FORM_39")}
            text={land?.form39 ? t("BPA_FILE_UPLOADED") : t("CS_NA")}
          />
          <Row
            label={t("BPA_TOD_ZONE")}
            text={checkForNA(land?.todZone?.name)}
          />
        </StatusTable>
        {window.location.href.includes("editApplication") ? (
          <React.Fragment>
            <StatusTable>
              <CardLabel>{t("BPA_DOCUMENT_DETAILS_LABEL")}</CardLabel>
              <LinkButton
                label={
                  <EditIcon
                    style={{
                      marginTop: "-10px",
                      float: "right",
                      position: "relative",
                      bottom: "32px",
                    }}
                  />
                }
                style={{ width: "100px", display: "inline" }}
                onClick={() => routeTo(`${routeLink}/document-details`)}
              />
              {
                <DocumentsPreview
                  documents={getOrderDocuments(applicationDocs)}
                  svgStyles={{}}
                  isSendBackFlow={false}
                  isHrLine={true}
                  titleStyles={{
                    fontSize: "18px",
                    lineHeight: "24px",
                    fontWeight: 700,
                    marginBottom: "10px",
                  }}
                />
              }
            </StatusTable>
            
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <CardLabel style={{ fontSize: "18px", marginTop: "24px", fontWeight: "bold" }}>{t("FORM_22_DETAILS")}</CardLabel>
                {!isExpanded && (
                  <LinkButton
                    label={t("VIEW_DETAILS")}
                    onClick={toggleExpanded}
                    style={{ marginRight: "1rem" }}
                  />
                )}
              </div>

              {isExpanded && (
                <React.Fragment>
                  <StatusTable>
                  {getDetailsRow(value?.form)}

                  <div style={{ marginTop: "1rem" }}>
                    <LinkButton
                      label={t("COLLAPSE")}
                      onClick={toggleExpanded}
                    />
                  </div>
                  </StatusTable>
                </React.Fragment>
              )}
           

            <StatusTable>
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <CardLabel style={{ fontSize: "18px", marginTop: "24px", fontWeight: "bold" }}>{t("FORM_23A_DETAILS")}</CardLabel>
                {!isExpanded && (
                  <LinkButton
                    label={t("VIEW_DETAILS")}
                    onClick={toggleExpanded}
                    style={{ marginRight: "1rem" }}
                  />
                )}
              </div>

              {isExpanded && (
                <React.Fragment>
                  {getDetailsRow(value?.form23A)}

                  <div style={{ marginTop: "1rem" }}>
                    <LinkButton
                      label={t("COLLAPSE")}
                      onClick={toggleExpanded}
                    />
                  </div>
                </React.Fragment>
              )}
            </StatusTable>
            <StatusTable>
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <CardLabel style={{ fontSize: "18px", marginTop: "24px", fontWeight: "bold" }}>{t("FORM_23B_DETAILS")}</CardLabel>
                {!isExpanded && (
                  <LinkButton
                    label={t("VIEW_DETAILS")}
                    onClick={toggleExpanded}
                    style={{ marginRight: "1rem" }}
                  />
                )}
              </div>

              {isExpanded && (
                <React.Fragment>
                  {getDetailsRow(value?.form23B)}

                  <div style={{ marginTop: "1rem" }}>
                    <LinkButton
                      label={t("COLLAPSE")}
                      onClick={toggleExpanded}
                    />
                  </div>
                </React.Fragment>
              )}
            </StatusTable>
          </React.Fragment>
        ) : null}

        <div
          style={{
            marginTop: "24px",
            padding: "16px",
            border: "1px solid #ccc",
            borderRadius: "4px",
          }}
        >
          <CheckBox
            label={t("BPA_DECLARATION_MESSAGE").replace(
              "{applicantName}",
              applicant?.applicantName || t("CS_APPLICANT")
            )}
            onChange={setDeclarationHandler}
            checked={agree}
          />
        </div>

        <SubmitBar
          label={t("CS_COMMON_SUBMIT")}
          onSubmit={onSubmit}
          disabled={!agree}
          style={{ marginTop: "24px" }}
        />
      </Card>
    </React.Fragment>
  );
};

export default CheckPage;