package com.lealpoints.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public abstract class DbBuilder<T> {



    public void setValue(Object valueT) {

    }
    public Object[] getValue()

    {
        return  null;
    }
    public abstract String sql() throws SQLException;

    public Object[] values() {
        return new Object[0];
    }

    public abstract T build(ResultSet resultSet) throws SQLException;
}