import {useMutation } from "react-query";

import { OBPSV2Services } from "../../services/elements/OBPSV2"


// Custom hook for creating or updating BPA resources using react-query
export const useBPACreateUpdateApi = (tenantId, flow) => {
    // Return mutation for create or update based on the 'type' parameter
  if (flow==="create") {
   
    return useMutation((data) => 
        OBPSV2Services.create(data, tenantId));
    
  } 
    // If type is false, return mutation for update
    return useMutation((data) => OBPSV2Services.update(data, tenantId));

};

export default useBPACreateUpdateApi;