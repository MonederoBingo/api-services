package com.lealpoints.environments;

public abstract class Environment {

    public abstract String getDatabasePath();

    public abstract String getDatabaseDriverClass();

    public abstract String getDatabaseUsername();

    public abstract String getDatabasePassword();

    public abstract String getImageDir();

    public abstract String getSchema();

    public abstract String getURIPrefix();

    @Deprecated
    public abstract String getClientUrl();
}
