package com.hoangdp.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TagsSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("tag_id", table, columnPrefix + "_tag_id"));
        columns.add(Column.aliased("owner", table, columnPrefix + "_owner"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));

        columns.add(Column.aliased("profiles_profile_id", table, columnPrefix + "_profiles_profile_id"));
        return columns;
    }
}
