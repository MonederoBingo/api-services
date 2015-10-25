package com.lealpoints.repository;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.queryagent.QueryAgent;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseRepository {

    @Autowired
    ThreadContextService _threadContextService;


    protected QueryAgent getQueryAgent() throws Exception {
        return _threadContextService.getQueryAgent();
    }

    /**
     * Returns an sql expression to encrypt a word
     *
     * @param column        Column name to be compared to
     * @param wordToEncrypt String to encrypt
     * @return The encrypted string
     */
    protected String encryptForSelect(String column, String wordToEncrypt) {
        return "crypt('" + wordToEncrypt + "', " + column + ")";
    }

    protected String encryptForUpdate(String wordToEncrypt) {
        return "crypt('" + wordToEncrypt + "', gen_salt('bf'))";
    }

}