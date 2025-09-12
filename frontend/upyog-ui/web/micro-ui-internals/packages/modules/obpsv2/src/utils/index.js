export const convertDateToEpoch = (dateString, dayStartOrEnd = "dayend") => {
  //example input format : "2018-10-02"
  try {
    const parts = dateString.match(/(\d{4})-(\d{1,2})-(\d{1,2})/);
    const DateObj = new Date(Date.UTC(parts[1], parts[2] - 1, parts[3]));
    DateObj.setMinutes(DateObj.getMinutes() + DateObj.getTimezoneOffset());
    if (dayStartOrEnd === "dayend") {
      DateObj.setHours(DateObj.getHours() + 24);
      DateObj.setSeconds(DateObj.getSeconds() - 1);
    }
    return DateObj.getTime();
  } catch (e) {
    return dateString;
  }
};

export const convertEpochToDateDMY = (dateEpoch) => {
  if (dateEpoch == null || dateEpoch == undefined || dateEpoch == "") {
    return "NA";
  }
  const dateFromApi = new Date(dateEpoch);
  let month = dateFromApi.getMonth() + 1;
  let day = dateFromApi.getDate();
  let year = dateFromApi.getFullYear();
  month = (month > 9 ? "" : "0") + month;
  day = (day > 9 ? "" : "0") + day;
  return `${day}/${month}/${year}`;
};

export const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
  if (searcher == "") return str;
  while (str.includes(searcher)) {
    str = str.replace(searcher, replaceWith);
  }
  return str;
};
export const checkForNotNull = (value = "") => {
  return value && value != null && value != undefined && value != "" ? true : false;
};

export const checkForNA = (value = "") => {
  return checkForNotNull(value) ? value : "CS_NA";
};

export const bpaPayload = (data) =>{
  const formdata={
    BPA: {
        tenantId: data?.tenantId,
        areaMapping:{
          buildingPermitAuthority: data?.areaMapping?.bpAuthority?.code,
          district: data?.areaMapping?.district?.code,
          mouza: data?.areaMapping?.mouza?.code || data?.areaMapping?.mouza,
          planningArea: data?.areaMapping?.planningArea?.code,
          planningPermitAuthority: data?.areaMapping?.ppAuthority?.code,
          revenueVillage: data?.areaMapping?.revenueVillage?.code,
          ward: data?.areaMapping?.ward
        },

        documents: data?.land?.documents?.map((doc) =>({
          ...doc,
        })) || [],
        
        landInfo:{
          address:{
            addressLine1: data?.address?.permanent?.addressLine1,
            addressLine2: data?.address?.permanent?.addressLine2,
            city: data?.address?.permanent?.city?.code,
            country: "INDIA",
            district: data?.address?.permanent?.district?.code,
            houseNo: data?.address?.permanent?.houseNo,
            pincode: data?.address?.permanent?.pincode,
            state: data?.address?.permanent?.state?.code,
            tenantId: data?.tenantId
          },
            documents: data?.land?.documents?.map((doc) =>({
              ...doc,
            })) || [],
            newDagNumber: data?.land?.newDagNumber,
            newPattaNumber: data?.land?.newPattaNumber,
            oldDagNumber: data?.land?.oldDagNumber,
            oldPattaNumber: data?.land?.oldPattaNumber,
            totalPlotArea: data?.land?.totalPlotArea,
            owners:{
              aadhaarNumber: data?.applicant?.aadhaarNumber,
              panNumber:data?.applicant?.panCardNumber,
              mobileNumber: data?.applicant?.mobileNumber,
              altContactNumber: data?.applicant?.alternateNumber,
              name: data?.applicant?.applicantName,
              emailId: data?.applicant?.emailId,
              fatherOrHusbandName: data?.applicant?.fatherName,
              motherName: data?.applicant?.motherName
            },
            rtpDetails:{
              rtpCategory: data?.land?.rtpCategory?.code,
              rtpName: data?.land?.registeredTechnicalPerson?.code
            },
            units:{
              occupancyType: data?.land?.occupancyType?.code,
            }
          },
        workflow:{
          action:"APPLY",
          comments:""
        }
    },
  };
  return formdata;
};

export const getOrderDocuments = (appUploadedDocumnets, isNoc = false) => {
  let finalDocs = [];
  if (appUploadedDocumnets?.length > 0) {
    let uniqueDocmnts = appUploadedDocumnets.filter((elem, index) => appUploadedDocumnets.findIndex((obj) => obj?.documentType?.split(".")?.slice(0, 2)?.join("_") === elem?.documentType?.split(".")?.slice(0, 2)?.join("_")) === index);
    uniqueDocmnts?.map(uniDoc => {
      const resultsDocs = appUploadedDocumnets?.filter(appDoc => uniDoc?.documentType?.split(".")?.slice(0, 2)?.join("_") == appDoc?.documentType?.split(".")?.slice(0, 2)?.join("_"));
      resultsDocs?.forEach(resDoc => resDoc.title = resDoc.documentType);
      finalDocs.push({
        title: !isNoc ? resultsDocs?.[0]?.documentType?.split(".")?.slice(0, 2)?.join("_") : "",
        values: resultsDocs
      })
    });
  }
  return finalDocs;
}