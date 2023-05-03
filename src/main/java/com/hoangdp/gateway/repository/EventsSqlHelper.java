package com.hoangdp.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class EventsSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("event_id", table, columnPrefix + "_event_id"));
        columns.add(Column.aliased("owner", table, columnPrefix + "_owner"));
        columns.add(Column.aliased("kind", table, columnPrefix + "_kind"));
        columns.add(Column.aliased("date", table, columnPrefix + "_date"));
        columns.add(Column.aliased("is_lunar", table, columnPrefix + "_is_lunar"));

        columns.add(Column.aliased("profiles_profile_id", table, columnPrefix + "_profiles_profile_id"));
        return columns;
    }
}
