package {service.namespace}.utils.sap.netweaver;

import com.sap.security.api.ISearchResult;

public class AbstractUMEApplicationPrincipalProvider {

    /**
     * get Search Result explained.
     * 
     * @param state
     * @return
     */
    protected String searchResultStateAsTest(int state) {
        String explainedText = "Unknown";
        switch (state) {
        case ISearchResult.SEARCH_RESULT_OK:
            explainedText = "SEARCH_RESULT_OK";
            break;
        case ISearchResult.SEARCH_RESULT_INCOMPLETE:
            explainedText = "SEARCH_RESULT_INCOMPLETE";
            break;
        case ISearchResult.SEARCH_RESULT_UNDEFINED:
            explainedText = "SEARCH_RESULT_UNDEFINED";
            break;
        case ISearchResult.SIZE_LIMIT_EXCEEDED:
            explainedText = "SIZE_LIMIT_EXCEEDED";
            break;
        case ISearchResult.TIME_LIMIT_EXCEEDED:
            explainedText = "TIME_LIMIT_EXCEEDED";
            break;
        }
        explainedText += "(" + state + ")";
        return explainedText;
    }
}
