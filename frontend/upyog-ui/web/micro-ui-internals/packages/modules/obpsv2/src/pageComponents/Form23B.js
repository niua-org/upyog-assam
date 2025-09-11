import React, { useEffect, useState } from "react";
import {
  FormStep,
  TextInput,
  CardLabel,
  Card,
  CardSubHeader,
  DatePicker
} from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";

const Form23B = ({ config, onSelect, userType, formData, value = formData }) => {
  const { t } = useTranslation();

  // State for fields
  const [purpose, setPurpose] = useState();
  const [noOfInhabitants, setnoOfInhabitants] = useState();
  const [waterSource, setWaterSource] = useState();
  const [distanceFromSewer, setDistanceFromSewer] = useState();
  const [materials, setMaterials] = useState();
  const [architectName, setarchitectName] = useState();
  const [registrationNumber, setRegistrationNumber] = useState();
  const [architectAddress, setArchitectAddress] = useState();
  const [constructionValidUpto, setConstructionValidUpto] = useState();
  const [leaseExtensionUpto, setLeaseExtensionUpto ] = useState();
  const [dwellingUnitSize, setdwellingUnitSize] = useState();
  const [noOfBathrooms, setnoOfBathrooms] = useState();
  // State for table
  const [floorArea, setFloorArea] = useState([
    { floor: "Basement", existing: "", proposed: "", total: "" },
    
    { floor: "Ground", existing: "", proposed: "", total: "" },
    { floor: "Mezzanine", existing: "", proposed: "", total: "" },
    { floor: "First floor", existing: "", proposed: "", total: "" },
    { floor: "Second floor", existing: "", proposed: "", total: "" },
    { floor: "Third", existing: "", proposed: "", total: "" },
   
    { floor: "Service floor (if any)", existing: "", proposed: "", total: "" },
  ]);

  // Table input change handler
  const handleFloorAreaChange = (index, field, value) => {
    const updated = [...floorArea];
    updated[index][field] = value;
    setFloorArea(updated);
  };

  // Compact table styles
  const tableStyle = { width: "80%", borderCollapse: "collapse", border: "1px solid #ccc" }

  const cellStyle = {
    border: "1px solid #ccc",
    padding: "8px",
    textAlign: "left",
    fontSize: "14px",
  };

  // Save form data
  const goNext = () => {
    let formStepData = {
      purpose,
      noOfInhabitants,
      noOfBathrooms,
      waterSource,
      distanceFromSewer,
      materials,
      architectName,
      registrationNumber,
      architectAddress,
      dwellingUnitSize,
      floorArea,
      constructionValidUpto,
      leaseExtensionUpto
    };

    if (userType === "citizen") {
      onSelect(config.key, { ...formData[config.key], ...formStepData });
    } else {
      onSelect(config.key, formStepData);
    }
  };

  const onSkip = () => onSelect();

  // Auto-save for citizens
  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, []);

  return (
    <React.Fragment>
      <Card>
        <CardSubHeader style={{ textAlign: "center" }}>
          <h2>{t("Form 23")}</h2>
          <div style={{ fontSize: "14px", color: "#555" }}>
            {t("(BPA_ANNEXURE_DESCRIPTION_B)")}
          </div>
        </CardSubHeader>
      </Card>

      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={false}
      >
        <CardLabel>
          (1) {t("THE_PURPOSE")}{" "}
          
        </CardLabel>
        <TextInput
          value={purpose}
          onChange={(e) => setPurpose(e.target.value)}
          placeholder={t("Text Input")}
        />

        <CardLabel>
          (2) {t("DETAILS_OF_AREA")}{" "}
        </CardLabel>
        <div style={{ overflowX: "auto", marginBottom: "1rem" }}>

          <table style={tableStyle}>
            <thead>
              <tr style={{ backgroundColor: "#f0f0f0" }}>
                <th style={cellStyle}>Floor</th>
                <th style={cellStyle}>
                  Existing
                  <br />
                  (sq. m.)
                </th>
                <th style={cellStyle}>
                  Proposed
                  <br />
                  (sq. m.)
                </th>
                <th style={cellStyle}>
                  Total
                  <br />
                  (sq. m.)
                </th>
              </tr>
            </thead>
            <tbody>
              {floorArea.map((row, index) => (
                <tr key={index}>
                  <td style={cellStyle}>{row.floor}</td>
                  <td style={cellStyle}>
                    <input
                      type="text"
                      value={row.existing}
                      onChange={(e) =>
                        handleFloorAreaChange(index, "existing", e.target.value)
                      }
                      
                    />
                  </td>
                  <td style={cellStyle}>
                    <input
                      type="text"
                      value={row.proposed}
                      onChange={(e) =>
                        handleFloorAreaChange(index, "proposed", e.target.value)
                      }
    
                    />
                  </td>
                  <td style={cellStyle}>
                    <input
                      type="text"
                      value={row.total}
                      onChange={(e) =>
                        handleFloorAreaChange(index, "total", e.target.value)
                      }
                      
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <br/>
        </div>

        <CardLabel>
          (3) (a) {t("NO_OF_INHABITANTS")}{" "}
        </CardLabel>
        <TextInput
          value={noOfInhabitants}
          onChange={(e) => setnoOfInhabitants(e.target.value)}
          placeholder={t("Text Input")}
        />
        <CardLabel>
            (b) {t("NO_OF_BATHROOMS")}{" "}
        </CardLabel>
        <TextInput
          value={noOfBathrooms}
          onChange={(e) => setnoOfBathrooms(e.target.value)}
          placeholder={t("Text Input")}
        />

        <CardLabel>
            (c) {t("SOURCE_OF_WATER")}{" "}
        </CardLabel>
        <TextInput
          value={waterSource}
          onChange={(e) => setWaterSource(e.target.value)}
          placeholder={t("Text Input")}
        />

        <CardLabel>
            (d) {t("DISTANCE_FROM_PUBLIC_SEWER")}{" "}
          
        </CardLabel>
        <TextInput
          value={distanceFromSewer}
          onChange={(e) => setDistanceFromSewer(e.target.value)}
          placeholder={t("Text Input")}
        />

        <CardLabel>
            (e) {t("MATERIALS_TO_BE_USED")}{" "}
          
        </CardLabel>
        <TextInput
          value={materials}
          onChange={(e) => setMaterials(e.target.value)}
          placeholder={t("Text Input")}
        />

        <CardLabel style={{ fontSize: "14px", marginLeft: "24px" }}>
          (I) {t("NAME_OF_REGISTERED_ARCHITECT")}{" "}
         
        </CardLabel>
        <TextInput
        style={{ fontSize: "14px", marginLeft: "24px" }}
          value={architectName}
          onChange={(e) => setarchitectName(e.target.value)}
          placeholder={t("Text Input")}
        />

        <CardLabel style={{ fontSize: "14px", marginLeft: "24px" }}>
          (II) {t("REGISTRATION_NO_OF_ARCHITECT")}{" "}
         
        </CardLabel>
        <TextInput
          style={{ fontSize: "14px", marginLeft: "24px" }}
          value={registrationNumber}
          onChange={(e) => setRegistrationNumber(e.target.value)}
          placeholder={t("Text Input")}
        />

        <CardLabel style={{ fontSize: "14px", marginLeft: "24px" }}>
          (III) {t("ADDRESS_OF_ARCHITECT")}{" "}
          
        </CardLabel>
        <TextInput
          style={{ fontSize: "14px", marginLeft: "24px" }}
          value={architectAddress}
          onChange={(e) => setArchitectAddress(e.target.value)}
          placeholder={t("Text Input")}
        />
        <div
          style={{
            display: "flex",
            alignItems: "center",
            flexWrap: "wrap",   // ✅ allows wrapping if needed
            gap: "12px",
            marginBottom: "1rem",
            width: "100%",      // ✅ keeps content inside card box
          }}
       >
        
            (4) The period of construction valid up to
       
       
          <DatePicker style={{width:"30%"}}
            date={constructionValidUpto}
            onChange={(date) => setConstructionValidUpto(date)}
            name="constructionValidUpto"
          />
          as per the lease condition/further extension of the time for construction granted by the leaser is valid up to
            <DatePicker style={{width:"30%"}}
              date={leaseExtensionUpto}
              onChange={(date) => setLeaseExtensionUpto(date)}
              name="leaseExtensionUpto"
            />
         Time construction obtained from the Competent Authority.
        </div>
        <CardLabel >
         (5) {t("SIZE_OF_DWELLING_UNIT")}{" "}
        </CardLabel>
        <TextInput
          value={dwellingUnitSize}
          onChange={(e) => setdwellingUnitSize(e.target.value)}
          placeholder={t("Text Input")}
        />
      </FormStep>
    </React.Fragment>
  );
};

export default Form23B;