package com.lealpoints.db.fixtures;

public class QueryAgentFixture {

    public String createDummyTable()
    {
        return "CREATE TABLE IF NOT EXISTS dummy (\n" +
                "  dummy_id    SERIAL PRIMARY KEY,\n" +
                "  name        TEXT,\n" +
                "  description TEXT,\n" +
                "  value       TEXT\n" +
                ");";
    }
}
