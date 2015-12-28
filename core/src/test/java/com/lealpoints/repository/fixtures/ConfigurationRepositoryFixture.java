package com.lealpoints.repository.fixtures;

public class ConfigurationRepositoryFixture {

    public String insertConfiguration()
    {
        return "INSERT INTO configuration (name, description, value) VALUES\n" +
                "  ('name1', 'desc1', 'value1'),\n" +
                "  ('name2', 'desc2', 'value2'),\n" +
                "  ('name3', 'desc3', 'value3');";
    }
}
