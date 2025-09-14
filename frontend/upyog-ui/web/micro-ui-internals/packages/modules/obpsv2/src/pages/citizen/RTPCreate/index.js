import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
import { newConfig } from "../../../config/rtpConfig";
import { uuidv4 } from "../../../utils";
const RTPCreate = () => {
console.log("u r in rtp craete")
  const queryClient = useQueryClient();
    const match = useRouteMatch();
    const { t } = useTranslation();
    const { pathname } = useLocation();
    const history = useHistory();
    let config = [];
    const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("RTP_CREATE", {});
    const [isShowToast, setIsShowToast] = useState(null);
    const [isSubmitBtnDisable, setIsSubmitBtnDisable] = useState(false);
    Digit.SessionStorage.set("RTP_BACK", "IS_RTP_BACK");
  
    const stateId = Digit.ULBService.getStateId();
    // let { data: newConfig } = Digit.Hooks.obps.SearchMdmsTypes.getFormConfig(stateId, []);
  
    function handleSelect(key, data, skipStep, index) {
      setIsSubmitBtnDisable(true);
      const loggedInuserInfo = Digit.UserService.getUser();
      const userInfo = { id: loggedInuserInfo?.info?.uuid, tenantId: loggedInuserInfo?.info?.tenantId };
      let RTPRequest = {
        transactionNumber: "",
        rtpNumber: "",
        planFile: null,
        tenantId: "",
        RequestInfo: {
          apiId: "",
          ver: "",
          ts: "",
          action: "",
          did: "",
          authToken: "",
          key: "",
          msgId: "",
          correlationId: "",
          userInfo: userInfo
        }
      };
  
      const applicantName = data?.applicantName;
      const file = data?.file;
      const tenantId = data?.tenantId?.code;
      const transactionNumber = uuidv4();
      const appliactionType = "BUILDING_PLAN_SCRUTINY";
      const applicationSubType = "NEW_CONSTRUCTION";
  
      RTPRequest = { ...RTPRequest, tenantId };
      RTPRequest = { ...RTPRequest, transactionNumber };
      RTPRequest = { ...RTPRequest, applicantName };
      RTPRequest = { ...RTPRequest, appliactionType };
      RTPRequest = { ...RTPRequest, applicationSubType };
  
      let bodyFormData = new FormData();
      bodyFormData.append("RTPRequest", JSON.stringify(RTPRequest));
      bodyFormData.append("planFile", file);
  
      Digit.OBPSV2Services.rtpcreate({ data: bodyFormData }, tenantId)
        .then((result, err) => {
          setIsSubmitBtnDisable(false);
          if (result) {
            setParams(result);
            history.replace(
              `/upyog-ui/citizen/obpsv2/rtp/apply/acknowledgement`, 
            );
          }
        })
        .catch((e) => {
          setParams({data: e?.response?.data?.errorCode ? e?.response?.data?.errorCode : "BPA_INTERNAL_SERVER_ERROR", type: "ERROR"});
          setIsSubmitBtnDisable(false);
          setIsShowToast({ key: true, label: e?.response?.data?.errorCode ? e?.response?.data?.errorCode : "BPA_INTERNAL_SERVER_ERROR" })
        });
  
    }
  
    const handleSkip = () => { };
    const handleMultiple = () => { };
  
    const onSuccess = () => {
      sessionStorage.removeItem("CurrentFinancialYear");
      queryClient.invalidateQueries("TL_CREATE_TRADE");
    };
    newConfig.forEach((obj) => {
      config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
    });
    config.indexRoute = "home";
  
    const RTPAcknowledgement = Digit?.ComponentRegistryService?.getComponent('RTPAcknowledgement') ;
  
    return (
      <Switch>
        {config.map((routeObj, index) => {
          const { component, texts, inputs, key } = routeObj;
          const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
          return (
            <Route path={`${match.path}/${routeObj.route}`} key={index}>
              <Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} onAdd={handleMultiple} isShowToast={isShowToast} isSubmitBtnDisable={isSubmitBtnDisable} setIsShowToast={setIsShowToast}/>
            </Route>
          );
        })}
        <Route path={`${match.path}/acknowledgement`}>
          <RTPAcknowledgement data={params} onSuccess={onSuccess} />
        </Route>
        <Route>
          <Redirect to={`${match.path}/${config.indexRoute}`} />
        </Route>
      </Switch>
    );
};

export default RTPCreate;