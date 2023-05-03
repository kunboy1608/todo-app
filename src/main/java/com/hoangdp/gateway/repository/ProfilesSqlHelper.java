package com.hoangdp.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProfilesSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("profile_id", table, columnPrefix + "_profile_id"));
        columns.add(Column.aliased("username", table, columnPrefix + "_username"));
        columns.add(Column.aliased("nickname", table, columnPrefix + "_nickname"));
        columns.add(Column.aliased("birthday", table, columnPrefix + "_birthday"));
        columns.add(Column.aliased("bio", table, columnPrefix + "_bio"));
        columns.add(Column.aliased("created_by", table, columnPrefix + "_created_by"));
        columns.add(Column.aliased("created_on", table, columnPrefix + "_created_on"));
        columns.add(Column.aliased("modified_by", table, columnPrefix + "_modified_by"));
        columns.add(Column.aliased("modified_on", table, columnPrefix + "_modified_on"));

        return columns;
    }
}
