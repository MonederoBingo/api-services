package com.lealpoints.environments;

public interface EnvironmentFactory {
    public DevEnvironment getDevEnvironment();

    public UnitTestEnvironment getUnitTestEnvironment();

    public FunctionalTestEnvironment getFunctionalTestEnvironment();

    public UATEnvironment getUATEnvironment();

    public ProdEnvironment getProdEnvironment();
}
