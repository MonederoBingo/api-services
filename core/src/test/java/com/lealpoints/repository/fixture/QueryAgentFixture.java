package com.lealpoints.repository.fixture;

public class QueryAgentFixture {

    public String getFixtureforQueryAgent()
    {
        return "CREATE TABLE IF NOT EXISTS dummy (\n" +
                "  dummy_id    SERIAL PRIMARY KEY,\n" +
                "  name        TEXT,\n" +
                "  description TEXT,\n" +
                "  value       TEXT\n" +
                ");";
    }
}
