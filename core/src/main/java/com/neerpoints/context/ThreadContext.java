package com.neerpoints.context;

import com.neerpoints.db.QueryAgent;

public class ThreadContext {
    private QueryAgent _clientQueryAgent;
    private String _language;
    private Environment _environment;

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

    public Environment getEnvironment() {
        return _environment;
    }

    public void setEnvironment(Environment environment) {
        _environment = environment;
    }
}
