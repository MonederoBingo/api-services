package com.lealpoints.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public abstract class DbBuilder<T> {




    public abstract String sql() throws SQLException;

    public abstract Object[] values();

    public abstract T build(ResultSet resultSet) throws SQLException;
}