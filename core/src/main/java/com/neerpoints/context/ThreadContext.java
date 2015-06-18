package com.neerpoints.context;

import com.neerpoints.db.QueryAgent;

public class ThreadContext {

    private QueryAgent _clientQueryAgent;
    private String _language;
    private boolean isProdEnvironment;

    public QueryAgent getClientQueryAgent() {
        return _clientQueryAgent;
    }

    public void setClientQueryAgent(QueryAgent clientQueryAgent) {
        _clientQueryAgent = clientQueryAgent;
    }

    public String getLanguage() {
        return _language;
    }

    public void setLanguage(String language) {
        _language = language;
    }

    public boolean isProdEnvironment() {
        return isProdEnvironment;
    }

    public void setProdEnvironment(boolean isProdEnvironment) {
        this.isProdEnvironment = isProdEnvironment;
    }
}
