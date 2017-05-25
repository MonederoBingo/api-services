package com.lealpoints;

public class InsertQuery
{
    private final String query;
    private final String idColumnName;

    public InsertQuery(String query, String idColumnName)
    {
        this.query = query;
        this.idColumnName = idColumnName;
    }

    public String getQuery()
    {
        return query;
    }

    public String getIdColumnName()
    {
        return idColumnName;
    }
}
