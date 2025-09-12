import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, Card, CardSubHeader } from "@upyog/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

const Form22A = ({ config, onSelect, userType, formData, value = formData }) => {
  const { pathname: url } = useLocation();
  const { t } = useTranslation();
  // State variables for each field
  const [plotArea, setPlotArea] = useState(formData?.plotArea|| formData?.form?.plotArea || "");
  const [existingPlinthArea, setExistingPlinthArea] = useState(formData?.existingPlinthArea || formData?.form?.existingPlinthArea|| "");
  const [proposedPlinthArea, setProposedPlinthArea] = useState(formData?.proposedPlinthArea || formData?.form?.proposedPlinthArea|| "");
  const [floorAreaCalculation, setFloorAreaCalculation] = useState(formData?.floorAreaCalculation || formData?.form?.floorAreaCalculation || "");
  const [mezzanineFloorArea, setMezzanineFloorArea] = useState(formData?.mezzanineFloorArea || formData?.form?.mezzanineFloorArea || "");
  const [deductionCalculation, setDeductionCalculation] = useState(formData?.deductionCalculation || formData?.form?.deductionCalculation || "");
  const [totalFloorAreaAfterDeduction, setTotalFloorAreaAfterDeduction] = useState(formData?.totalFloorAreaAfterDeduction || formData?.form?.totalFloorAreaAfterDeduction || "");
  const [totalFloorAreaBeforeDeduction, setTotalFloorAreaBeforeDeduction] = useState(formData?.totalFloorAreaBeforeDeduction || formData?.form?.totalFloorAreaBeforeDeduction || "");
  const [coverage, setCoverage] = useState(formData?.coverage || formData?.form?.coverage || "");
  const [floorAreaRatio, setFloorAreaRatio] = useState(formData?.floorAreaRatio || formData?.form?.floorAreaRatio || "");

  // Go next
  const goNext = () => {
    let formStepData = {
      plotArea,
      existingPlinthArea,
      proposedPlinthArea,
      floorAreaCalculation,
      mezzanineFloorArea,
      deductionCalculation,
      totalFloorAreaAfterDeduction,
      totalFloorAreaBeforeDeduction,
      coverage,
      floorAreaRatio,
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
          <h2>{t("Form 22")}</h2>
          <div style={{ fontSize: "14px", color: "#555" }}>
            {t("(For all categories of buildings)")}
          </div>
        </CardSubHeader>

        {/* <div style={{ textAlign: "right", color: "darkred", fontWeight: "bold", marginBottom: "15px" }}>
          *{t("Details will be auto fetched")}*
        </div> */}
      </Card>

      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={false}
      >
        {/* A. Plot Area */}
        <CardLabel>A. {t("PLOT_AREA")}</CardLabel>
        <TextInput value={plotArea} onChange={(e) => setPlotArea(e.target.value)} placeholder={t("Text Input")} />

        {/* B. Plinth Area */}
        <CardLabel>B. {t("PLINTH_AREA")}</CardLabel>

        <CardLabel style={{ fontSize: "14px", marginLeft: "20px" }}>
          I. {t("EXISTING_PLINTH_AREA")}
        </CardLabel>
        <TextInput value={existingPlinthArea} onChange={(e) => setExistingPlinthArea(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel style={{ fontSize: "14px", marginLeft: "20px" }}>
          II. {t("PROPOSED_PLINTH_AREA")}
        </CardLabel>
        <TextInput value={proposedPlinthArea} onChange={(e) => setProposedPlinthArea(e.target.value)} placeholder={t("Text Input")} />

        {/* C–I Other Fields */}
        <CardLabel>C. {t("FLOOR_AREA_DETAIL_CALCULATION")}</CardLabel>
        <TextInput value={floorAreaCalculation} onChange={(e) => setFloorAreaCalculation(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>D. {t("DETAIL_OF_MEZZANINE_FLOOR_AREA")}</CardLabel>
        <TextInput value={mezzanineFloorArea} onChange={(e) => setMezzanineFloorArea(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>E. {t("DEDUCTION_SHOWING_DETAIL_CALCULATION")}</CardLabel>
        <TextInput value={deductionCalculation} onChange={(e) => setDeductionCalculation(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>F. {t("TOTAL_FLOOR_AREA_AFTER_DEDUCTION")}</CardLabel>
        <TextInput value={totalFloorAreaAfterDeduction} onChange={(e) => setTotalFloorAreaAfterDeduction(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>G. {t("TOTAL_FLOOR_AREA_BEFORE_DEDUCTION")}</CardLabel>
        <TextInput value={totalFloorAreaBeforeDeduction} onChange={(e) => setTotalFloorAreaBeforeDeduction(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>H. {t("COVERAGE")}</CardLabel>
        <TextInput value={coverage} onChange={(e) => setCoverage(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>I. {t("FLOOR_AREA_RATIO")}</CardLabel>
        <TextInput value={floorAreaRatio} onChange={(e) => setFloorAreaRatio(e.target.value)} placeholder={t("Text Input")} />
      </FormStep>
    </React.Fragment>
  );
};

export default Form22A;