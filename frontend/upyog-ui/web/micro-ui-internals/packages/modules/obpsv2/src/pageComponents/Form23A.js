import React, { useEffect, useState } from "react";
import {
  FormStep,
  TextInput,
  CardLabel,
  Card,
  CardSubHeader,
  DatePicker,
} from "@upyog/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Timeline from "../components/Timeline";
const Form23A = ({ config, onSelect, userType, formData, value = formData }) => {
  const { pathname: url } = useLocation();
  const { t } = useTranslation();
  const [classificationOfProposal, setClassificationOfProposal] = useState(formData?.classificationOfProposal || formData?.form23A?.classificationOfProposal || "");
  const [revenueVillage, setRevenueVillage] = useState(formData?.revenueVillage || formData?.form23A?.revenueVillage || "");
  const [mouza, setMouza] = useState(formData?.mouza || formData?.form23A?.mouza || "");
  const [dagNo, setDagNo] = useState(formData?.dagNo || formData?.form23A?.dagNo || "");
  const [pattaNo, setPattaNo] = useState(formData?.pattaNo || formData?.form23A?.pattaNo || "");
  const [sitePlanArea, setSitePlanArea] = useState(formData?.sitePlanArea || formData?.form23A?.sitePlanArea || "");
  const [landDocumentArea, setLandDocumentArea] = useState(formData?.landDocumentArea || formData?.form23A?.landDocumentArea ||"");
  const [buildingHeight, setBuildingHeight] = useState(formData?.buildingHeight || formData?.form23A?.buildingHeight ||"");
  const [heightofPlinth, setHeightOfPlinth] = useState(formData?.heightofPlinth || formData?.form23A?.heightofPlinth || "");
  const [permitFee, setPermitFee] = useState(formData?.permitFee || formData?.form23A?.permitFee || "");
  const [cityInfrastructureCharges, setCityInfrastrutureCharges] = useState(formData?.cityInfrastructureCharges || formData?.form23A?.cityInfrastructureCharges || "");
  const [additionalFloorSpaceCharges, setAdditionalFloorSpaceCharges] = useState(formData?.additionalFloorSpaceCharges || formData?.form23A?.additionalFloorSpaceCharges ||"");
  const [peripheralCharges, setPeripheralCharges] = useState(formData?.peripheralCharges || formData?.form23A?.peripheralCharges || "");
  const [otherCharges, setOtherCharges] = useState(formData?.otherCharges || formData?.form23A?.otherCharges || "");
  const [totalAmount, setTotalAmount] = useState(formData?.totalAmount || formData?.form23A?.totalAmount|| "");
  const [receiptNo, setReceiptNo] = useState(formData?.receiptNo || formData?.form23A?.receiptNo ||"");
  const [dateValue, setDateValue] = useState(formData?.dateValue || formData?.form23A?.dateValue || "");
  const [tables, setTables] = useState({
    roadFacingPlot: formData?.roadFacingPlot || formData?.form23A?.roadFacingPlot|| [
      { existingWidth: "", proposedWidth: "", remarks: "" },
    ],
    principalBylaws: formData?.principalBylaws || [
      { desc: "Max Ground Coverage", proposed: "", use: "", permissible: "", carpetArea: "", remarks: "" },
      { desc: "Basement", proposed: "", use: "", permissible: "", carpetArea: "", remarks: "" },
      { desc: "No of floors", proposed: "", use: "", permissible: "", carpetArea: "", remarks: "" },
      { desc: "Service floor if any", proposed: "", use: "", permissible: "", carpetArea: "", remarks: "" },
      { desc: "Total floor area", proposed: "", use: "", permissible: "", carpetArea: "", remarks: "" },
      { desc: "Floor Area Ratio", proposed: "", use: "", permissible: "", carpetArea: "", remarks: "" },
      { desc: "No. of Dwelling units", proposed: "", use: "", permissible: "", carpetArea: "", remarks: "" },
    ],
    setbacks: formData?.setbacks || [
      { side: "Front", clear: "", cantilever: "", reqClear: "", reqCantilever: "", remarks: "" },
      { side: "Rear", clear: "", cantilever: "", reqClear: "", reqCantilever: "", remarks: "" },
      { side: "Left", clear: "", cantilever: "", reqClear: "", reqCantilever: "", remarks: "" },
      { side: "Right", clear: "", cantilever: "", reqClear: "", reqCantilever: "", remarks: "" },
    ],
    ducts: formData?.ducts || [{ no: "", area: "", width: "" }],
    electricLine: formData?.electricLine || [{ nature: "", verticalDistance: "", horizontalDistance: "" }],
    parkingProvided: formData?.parkingProvided || [{ open: "", stilt: "", basement: "", total: "" }],
    parkingRequired: formData?.parkingRequired || [
      { type: "", car: "", scooter: "", remarks: "" },
      { type: "", car: "", scooter: "", remarks: "" },
      { type: "", car: "", scooter: "", remarks: "" },
    ],
    visitorsParking: formData?.visitorsParking || [
      { type: "", car: "", scooter: "" },
      { type: "", car: "", scooter: "" },
      { type: "", car: "", scooter: "" },
    ],
  });

  const handleTableChange = (tableKey, rowIndex, field, value) => {
    const updated = { ...tables };
    updated[tableKey][rowIndex][field] = value;
    setTables(updated);
  };

  const cellStyle = {
    border: "1px solid #ccc",
    padding: "8px",
    textAlign: "left",
    fontSize: "14px",
  };

  const goNext = () => {
    let formStepData = {
      classificationOfProposal,
      revenueVillage,
      mouza,
      dagNo,
      pattaNo,
      sitePlanArea,
      landDocumentArea,
      buildingHeight,
      heightofPlinth,
      permitFee,
      cityInfrastructureCharges,
      additionalFloorSpaceCharges,
      peripheralCharges,
      otherCharges,
      totalAmount,
      receiptNo,
      dateValue,
      ...tables,
    };

    if (userType === "citizen") {
      onSelect(config.key, { ...formData[config.key], ...formStepData });
    } else {
      onSelect(config.key, formStepData);
    }
  };

  const onSkip = () => onSelect();

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, []);

  return (
    <React.Fragment>
       <Timeline currentStep={6} flow={"editApplication"}/>
      <Card>
        <CardSubHeader style={{ textAlign: "center" }}>
          <h2>{t("Form 23")}</h2>
          <div style={{ fontSize: "14px", color: "#555" }}>
            {t("(BPA_ANNEXURE_DESCRIPTION_A)")}
          </div>
          <div style={{ fontSize: "14px", color: "#555" }}>
          {t("FOR_ABOVE_G_2")}
        </div>
        </CardSubHeader>
        
      </Card>

      <FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t} isDisabled={false}>
      
        <CardLabel>{t("CLASSIFICATION_OF_PROPOSAL")}</CardLabel>
        <TextInput value={classificationOfProposal} onChange={(e) => setClassificationOfProposal(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>{t("REVENUE_VILLAGE")}</CardLabel>
        <TextInput value={revenueVillage} onChange={(e) => setRevenueVillage(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>{t("MOUZA")}</CardLabel>
        <TextInput value={mouza} onChange={(e) => setMouza(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>{t("DAG_NO")}</CardLabel>
        <TextInput value={dagNo} onChange={(e) => setDagNo(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>{t("PATTA_NO")}</CardLabel>
        <TextInput value={pattaNo} onChange={(e) => setPattaNo(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>{t("ROAD_FACING_THE_PLOT")}</CardLabel>
        <CardLabel style={{ fontSize: "14px", marginLeft: "20px" }}> (1) {t("EXISTING_ROAD_WIDTH")}</CardLabel>
        <table  
          style={{
            borderCollapse: "collapse",
            border: "1px solid #ccc",
            tableLayout: "auto",   
            width: "80%",        
            fontSize: "12px",
            lineHeight: "1.2",
        }}>
          <thead>
            <tr style={{ backgroundColor: "#f0f0f0" }}>
              <th style={cellStyle}>Sl. No.</th>
              <th style={cellStyle}>Existing road width</th>
              <th style={cellStyle}>Proposed road width</th>
              <th style={cellStyle}>Remarks</th>
            </tr>
          </thead>
          <tbody>
            {tables.roadFacingPlot.map((row, idx) => (
              <tr key={idx}>
                <td style={cellStyle}>{idx + 1}</td>
                <td style={cellStyle}>
                  <input type="text" value={row.existingWidth} onChange={(e) => handleTableChange("roadFacingPlot", idx, "existingWidth", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.proposedWidth} onChange={(e) => handleTableChange("roadFacingPlot", idx, "proposedWidth", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.remarks} onChange={(e) => handleTableChange("roadFacingPlot", idx, "remarks", e.target.value)} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <br />
        <CardLabel>(1) {t("PLOT_AREA")}</CardLabel>
        <CardLabel style={{ fontSize: "14px", marginLeft: "20px" }}>a. {t("AS_PER_SITE_PLAN")}</CardLabel>
        <TextInput style={{ fontSize: "14px", marginLeft: "20px" }} value={sitePlanArea} onChange={(e) => setSitePlanArea(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel style={{ fontSize: "14px", marginLeft: "20px" }}>b. {t("AS_PER_LAND_DOCUMENT")}</CardLabel>
        <TextInput style={{ fontSize: "14px", marginLeft: "20px" }} value={landDocumentArea} onChange={(e) => setLandDocumentArea(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>{t("PRINCIPAL_BY_LAWS")}</CardLabel>
        <CardLabel>{t("ROAD_FACING_THE_PLOT")}</CardLabel>
        <CardLabel style={{ fontSize: "14px", marginLeft: "20px" }}>(1) {t("EXISTING_ROAD_WIDTH")}</CardLabel>
        <table style={{ width: "80%", borderCollapse: "collapse", border: "1px solid #ccc" }}>
          <thead>
            <tr style={{ backgroundColor: "#f0f0f0" }}>
              <th style={cellStyle}>Description</th>
              <th style={cellStyle}>Proposed Sqm</th>
              <th style={cellStyle}>Use</th>
              <th style={cellStyle}>Permissible</th>
              <th style={cellStyle}>Carpet Area</th>
              <th style={cellStyle}>Remarks</th>
            </tr>
          </thead>
          <tbody>
            {tables.principalBylaws.map((row, idx) => (
              <tr key={idx}>
                <td style={cellStyle}>{row.desc}</td>
                <td style={cellStyle}>
                  <input type="text" value={row.proposed} onChange={(e) => handleTableChange("principalBylaws", idx, "proposed", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.use} onChange={(e) => handleTableChange("principalBylaws", idx, "use", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.permissible} onChange={(e) => handleTableChange("principalBylaws", idx, "permissible", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.carpetArea} onChange={(e) => handleTableChange("principalBylaws", idx, "carpetArea", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.remarks} onChange={(e) => handleTableChange("principalBylaws", idx, "remarks", e.target.value)} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <br/>

        <CardLabel style={{ fontSize: "14px", marginLeft: "20px" }}>C. {t("MAXIMUM_HEIGHT_OF_BUILDING")}</CardLabel>
        <TextInput style={{ fontSize: "14px", marginLeft: "20px" }} value={buildingHeight} onChange={(e) => setBuildingHeight(e.target.value)} placeholder={t("Text Input")} /><br/>

        <CardLabel style={{ fontSize: "14px", marginLeft: "20px" }}>D. {t("MAXIMUM_HEIGHT_OF_PLINTH")}</CardLabel>
        <TextInput style={{ fontSize: "14px", marginLeft: "20px" }} value={heightofPlinth} onChange={(e) => setHeightOfPlinth(e.target.value)} placeholder={t("Text Input")} /><br/>

        <CardLabel>(4) {t("SETBACKS")}</CardLabel>
        <table style={{ width: "80%", borderCollapse: "collapse", border: "1px solid #ccc" }}>
          <thead>
            <tr style={{ backgroundColor: "#f0f0f0" }}>
              <th style={cellStyle}>Setbacks</th>
              <th style={cellStyle}>Clear setback</th>
              <th style={cellStyle}>Cantilever projection</th>
              <th style={cellStyle}>Req. Clear setback</th>
              <th style={cellStyle}>Req. Cantilever</th>
              <th style={cellStyle}>Remarks</th>
            </tr>
          </thead>
          <tbody>
            {tables.setbacks.map((row, idx) => (
              <tr key={idx}>
                <td style={cellStyle}>{row.side}</td>
                <td style={cellStyle}>
                  <input type="text" value={row.clear} onChange={(e) => handleTableChange("setbacks", idx, "clear", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.cantilever} onChange={(e) => handleTableChange("setbacks", idx, "cantilever", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.reqClear} onChange={(e) => handleTableChange("setbacks", idx, "reqClear", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.reqCantilever} onChange={(e) => handleTableChange("setbacks", idx, "reqCantilever", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.remarks} onChange={(e) => handleTableChange("setbacks", idx, "remarks", e.target.value)} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <br/>

        <CardLabel>(5) {t("DUCTS")}</CardLabel>
        <table style={{ width: "80%", borderCollapse: "collapse", border: "1px solid #ccc" }}>
          <thead>
            <tr style={{ backgroundColor: "#f0f0f0" }}>
              <th style={cellStyle}>No. of duct</th>
              <th style={cellStyle}>Area of duct</th>
              <th style={cellStyle}>Min width</th>
            </tr>
          </thead>
          <tbody>
            {tables.ducts.map((row, idx) => (
              <tr key={idx}>
                <td style={cellStyle}>
                  <input type="text" value={row.no} onChange={(e) => handleTableChange("ducts", idx, "no", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.area} onChange={(e) => handleTableChange("ducts", idx, "area", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.width} onChange={(e) => handleTableChange("ducts", idx, "width", e.target.value)} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <br/>

        
        <CardLabel>(6) {t("DISTANCE_FROM_ELECTRIC_LINE")}</CardLabel>
        <table style={{ width: "80%", borderCollapse: "collapse", border: "1px solid #ccc" }}>
          <thead>
            <tr style={{ backgroundColor: "#f0f0f0" }}>
              <th style={cellStyle}>Nature of line</th>
              <th style={cellStyle}>Area</th>
              <th style={cellStyle}>Max Width</th>
            </tr>
          </thead>
          <tbody>
            {tables.electricLine.map((row, idx) => (
              <tr key={idx}>
                <td style={cellStyle}>
                  <input type="text" value={row.nature} onChange={(e) => handleTableChange("electricLine", idx, "nature", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.verticalDistance} onChange={(e) => handleTableChange("electricLine", idx, "verticalDistance", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.horizontalDistance} onChange={(e) => handleTableChange("electricLine", idx, "horizontalDistance", e.target.value)} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <br/>

        <CardLabel>(7) {t("PARKING")}</CardLabel>
        <CardLabel style={{ fontSize: "14px", marginLeft: "20px" }}>(A) {t("PARKING_PROVIDED_AS_PER_BUILDING_BYE_LAWS")}</CardLabel>
        <table style={{ width: "80%", borderCollapse: "collapse", border: "1px solid #ccc" }}>
          <thead>
            <tr style={{ backgroundColor: "#f0f0f0" }}>
              <th style={cellStyle}>Open</th>
              <th style={cellStyle}>Stilt</th>
              <th style={cellStyle}>Basement</th>
              <th style={cellStyle}>Total</th>
            </tr>
          </thead>
          <tbody>
            {tables.parkingProvided.map((row, idx) => (
              <tr key={idx}>
                <td style={cellStyle}>
                  <input type="text" value={row.open} onChange={(e) => handleTableChange("parkingProvided", idx, "open", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.stilt} onChange={(e) => handleTableChange("parkingProvided", idx, "stilt", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.basement} onChange={(e) => handleTableChange("parkingProvided", idx, "basement", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.total} onChange={(e) => handleTableChange("parkingProvided", idx, "total", e.target.value)} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <br/>

        <CardLabel style={{ fontSize: "14px", marginLeft: "20px" }}>(B) {t("PARKING_REQUIRED_AS_PER_BUILDING_BYE_LAWS")}</CardLabel>
        <table style={{ width: "80%", borderCollapse: "collapse", border: "1px solid #ccc" }}>
          <thead>
            <tr style={{ backgroundColor: "#f0f0f0" }}>
              <th style={cellStyle}>Type</th>
              <th style={cellStyle}>Car</th>
              <th style={cellStyle}>Scooter</th>
              <th style={cellStyle}>Remarks</th>
            </tr>
          </thead>
          <tbody>
            {tables.parkingRequired.map((row, idx) => (
              <tr key={idx}>
                <td style={cellStyle}>
                  <input type="text" value={row.type} onChange={(e) => handleTableChange("parkingRequired", idx, "type", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.car} onChange={(e) => handleTableChange("parkingRequired", idx, "car", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.scooter} onChange={(e) => handleTableChange("parkingRequired", idx, "scooter", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.remarks} onChange={(e) => handleTableChange("parkingRequired", idx, "remarks", e.target.value)} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <br/>

        <CardLabel style={{ fontSize: "14px", marginLeft: "20px" }}>(C) {t("VISITORS_PARKING")}</CardLabel>
        <table style={{ width: "80%", borderCollapse: "collapse", border: "1px solid #ccc" }}>
          <thead>
            <tr style={{ backgroundColor: "#f0f0f0" }}>
              <th style={cellStyle}>Type</th>
              <th style={cellStyle}>Car</th>
              <th style={cellStyle}>Scooter</th>
            </tr>
          </thead>
          <tbody>
            {tables.visitorsParking.map((row, idx) => (
              <tr key={idx}>
                <td style={cellStyle}>
                  <input type="text" value={row.type} onChange={(e) => handleTableChange("visitorsParking", idx, "type", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.car} onChange={(e) => handleTableChange("visitorsParking", idx, "car", e.target.value)} />
                </td>
                <td style={cellStyle}>
                  <input type="text" value={row.scooter} onChange={(e) => handleTableChange("visitorsParking", idx, "scooter", e.target.value)} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <br/>
        <CardLabel>{t("BUILDING_AREA_NOTE")}</CardLabel>

        <br/>
        <CardLabel>(8) {t("FEE_AND_CHARGES")}</CardLabel>
        <CardLabel>{t("PERMIT_FEE")}</CardLabel>
        <TextInput value={permitFee} onChange={(e) => setPermitFee(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>{t("CITY_INFRASTRUCTURE_CHARGES")}</CardLabel>
        <TextInput value={cityInfrastructureCharges} onChange={(e) => setCityInfrastrutureCharges(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>{t("ADDITIONAL_FLOOR_SPACE_INDEX_CHARGES")}</CardLabel>
        <TextInput value={additionalFloorSpaceCharges} onChange={(e) => setAdditionalFloorSpaceCharges(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>{t("PERIPHERAL_CHARGES")}</CardLabel>
        <TextInput value={peripheralCharges} onChange={(e) => setPeripheralCharges(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>{t("OTHER_CHARGES")}</CardLabel>
        <TextInput value={otherCharges} onChange={(e) => setOtherCharges(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>{t("TOTAL_AMOUNT")}</CardLabel>
        <TextInput value={totalAmount} onChange={(e) => setTotalAmount(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>{t("RECEIPT_NO")}</CardLabel>
        <TextInput value={receiptNo} onChange={(e) => setReceiptNo(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>{t("DATE")}</CardLabel>
        <DatePicker date={dateValue} onChange={(e) => setDateValue(e)} />
      </FormStep>
    </React.Fragment>
  );
};

export default Form23A;