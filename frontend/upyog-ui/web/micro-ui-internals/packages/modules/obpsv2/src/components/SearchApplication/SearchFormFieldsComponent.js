import React, { Fragment } from "react";
import { TextInput, SubmitBar, DatePicker, SearchField, Dropdown, CardLabelError, MobileNumber } from "@upyog/digit-ui-react-components";
import { useWatch } from "react-hook-form";
import { useTranslation } from "react-i18next";
const SearchFormFieldsComponent = ({ formState, Controller, register, control,  reset, previousPage }) => {
const { t } = useTranslation();
  return (
    <>
      <SearchField>
        <label>{t("OBPAS_SEARCH_APPLICATION_NO_LABEL")}</label>
        <TextInput name="applicationNo" inputRef={null} />
      </SearchField>
      <SearchField>
        <label>{t("OBPAS_APP_MOBILE_NO_SEARCH_PARAM")}</label>
        <MobileNumber
            name="mobileNumber"
            disable={window.location.href.includes("obps/search/obps-application") ? true : false}
            inputRef={({
            minLength: {
              value: 10,
              message: t("CORE_COMMON_MOBILE_ERROR"),
            },
            maxLength: {
              value: 10,
              message: t("CORE_COMMON_MOBILE_ERROR"),
            },
            pattern: {
              value: /[6789][0-9]{9}/,
                      //type: "tel",
              message: t("CORE_COMMON_MOBILE_ERROR"),
            },
          })}
          type="number"
          componentInFront={<div className="employee-card-input employee-card-input--front">+91</div>}
                //maxlength={10}
        />
      {/* <CardLabelError>{formState?.errors?.["mobileNumber"]?.message}</CardLabelError> */}
      </SearchField>
      <SearchField>
        <label>{t("OBPAS_SEARCH_WARD_LABEL")}</label>
        <TextInput name="wardNo" inputRef={null} />
      </SearchField>
      <SearchField>
        <label>{t("OBPAS_APP_FROM_DATE_SEARCH_PARAM")}</label>
        <Controller render={(props) => <DatePicker date={props.value} onChange={props.onChange} />} name="fromDate" control={control} />
      </SearchField>
      <SearchField>
          <label>{t("OBPAS_APP_TO_DATE_SEARCH_PARAM")}</label>
          <Controller render={(props) => <DatePicker date={props.value} onChange={props.onChange} />} name="toDate" control={control} />
      </SearchField>
      <SearchField>
        <label>{t("OBPAS_SEARCH_APPLICATION_STATUS_LABEL")}</label>
        <Controller
          control={control}
          name="status"
          render={(props) => (
            <Dropdown selected={props.value} select={props.onChange} onBlur={props.onBlur} option={null} optionKey="i18nKey" t={t} />
          )}
        />
      </SearchField>
      <SearchField className="submit">
              <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
              <p
                style={{ marginTop: "24px" }}
                onClick={() => {
                  reset({
                    applicationNo: "",
                    mobileNumber: window.location.href.includes("/search/obps-application") ? currentUserPhoneNumber : "",
                    // mobileNumber: "",
                    fromDate: "",
                    toDate: "",
                    status: "",
                    offset: 0,
                    limit: 10,
                    sortBy: "commencementDate",
                    sortOrder: "DESC",
                    "isSubmitSuccessful":false,
                  });
                  previousPage();
                  // closeMobilePopupModal()
                }}
              >
                {t(`ES_COMMON_CLEAR_ALL`)}
              </p>
            </SearchField>
     
    </>
  );
};

export default SearchFormFieldsComponent;