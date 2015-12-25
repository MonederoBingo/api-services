package com.lealpoints.repository.fixture;

public class ConfigurationRepositoryFixture {

    public String getFixturefortestGetAll()
    {
        return "INSERT INTO configuration (name, description, value) VALUES\n" +
                "  ('name1', 'desc1', 'value1'),\n" +
                "  ('name2', 'desc2', 'value2'),\n" +
                "  ('name3', 'desc3', 'value3');";
    }

    public String getFixturefortestGetValueByName()
    {
        return "INSERT INTO configuration (name, description, value) VALUES\n" +
                "  ('name1', 'desc1', 'value1'),\n" +
                "  ('name2', 'desc2', 'value2'),\n" +
                "  ('name3', 'desc3', 'value3');";
    }
}
