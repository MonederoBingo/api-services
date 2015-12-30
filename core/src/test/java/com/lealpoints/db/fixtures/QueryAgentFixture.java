package com.lealpoints.db.fixtures;

public class QueryAgentFixture {

    public static final String CREATE_DUMMY_TABLE = "CREATE TABLE IF NOT EXISTS dummy (" +
                "  dummy_id    SERIAL PRIMARY KEY," +
                "  name        TEXT," +
                "  description TEXT," +
                "  value       TEXT" +
                ");";
}
