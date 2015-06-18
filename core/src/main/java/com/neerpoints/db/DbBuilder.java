package com.neerpoints.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DbBuilder<T> {

    public abstract String sql();

    public Object[] values() {
        return new Object[0];
    }

    public abstract T build(ResultSet resultSet) throws SQLException;
}