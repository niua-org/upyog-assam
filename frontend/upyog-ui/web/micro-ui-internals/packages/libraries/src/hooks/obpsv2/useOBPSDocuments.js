
import useMDMS from "./useMDMS";
const useOBPSDocuments = (stateId, formData, beforeUploadDocuments) => {

    const { isLoading: bpaDocsLoading, data: bpaDocs } = useMDMS("as", "BPA", ["DocMapping"]);
    const { isLoading: commonDocsLoading, data: commonDocs } = useMDMS(stateId, "common-masters", ["DocumentType"]);
    const documents = beforeUploadDocuments;
    const { data, isLoading, error } = Digit.Hooks.useDocumentSearch( documents, { enabled: beforeUploadDocuments?.length > 0 ? true : false});
    if (data?.pdfFiles?.length > 0) {
        beforeUploadDocuments = data?.pdfFiles
    }


    let filtredBpaDocs = bpaDocs?.BPA?.DocMapping;
        
        let documentsList = [];
        filtredBpaDocs?.[0]?.docTypes?.forEach(doc => {
            let code = doc.code; doc.dropdownData = []; doc.uploadedDocuments = [];
            commonDocs?.["common-masters"]?.DocumentType?.forEach(value => {
                let values = value.code.slice(0, code.length);
                if (code === values) {
                    doc.hasDropdown = true;
                    value.i18nKey = value.code;
                    doc.dropdownData.push(value);
                }
            });
            
            doc.uploadedDocuments[0] = {};
            doc.uploadedDocuments[0].values = [];
            beforeUploadDocuments.map(upDocs => {
                if (code === `${upDocs?.documentType?.split('.')[0]}.${upDocs?.documentType?.split('.')[1]}`) {
                    doc.uploadedDocuments[0].values.push(upDocs)
                }
            })
            documentsList.push(doc);
        });
    return { data: documentsList , isLoading: bpaDocsLoading || commonDocsLoading }

}

export default useOBPSDocuments