package com.lealpoints.environments;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UATEnvironment extends Environment
{
    @Value("${db_driver}")
    private String dbDriver;

    @Value("${db_driver_class}")
    private String dbDriverClass;

    @Value("${JDBC_DATABASE_URL}")
    private String dbUrl;

    @Value("${JDBC_DATABASE_USERNAME}")
    private String dbUser;

    @Value("${JDBC_DATABASE_PASSWORD}")
    private String dbPassword;

    @Value("${uat.client_url}")
    private String clientUrl;

    @Value("${db_test_schema}")
    private String schema;

    @Override
    public String getDatabasePath()
    {
        return dbUrl;
    }

    public String getDatabaseDriverClass()
    {
        return dbDriverClass;
    }

    public String getDatabaseUsername()
    {
        return dbUser;
    }

    public String getDatabasePassword()
    {
        return dbPassword;
    }

    public String getImageDir()
    {
        return System.getenv("OPENSHIFT_DATA_DIR") + "images/uat/";
    }

    @Override
    public String getSchema()
    {
        return schema;
    }

    @Override public String getURIPrefix()
    {
        return "uat.";
    }

    public String getClientUrl()
    {
        return clientUrl;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof DevEnvironment))
        {
            return false;
        }
        DevEnvironment that = (DevEnvironment) obj;
        return getDatabasePath().equals(that.getDatabasePath());
    }

    @Override
    public int hashCode()
    {
        return getDatabasePath().hashCode();
    }
}
