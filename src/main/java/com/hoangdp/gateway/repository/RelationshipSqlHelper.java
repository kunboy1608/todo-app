package com.hoangdp.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class RelationshipSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("relationship_id", table, columnPrefix + "_relationship_id"));
        columns.add(Column.aliased("owner", table, columnPrefix + "_owner"));
        columns.add(Column.aliased("partner", table, columnPrefix + "_partner"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("created_by", table, columnPrefix + "_created_by"));
        columns.add(Column.aliased("created_on", table, columnPrefix + "_created_on"));
        columns.add(Column.aliased("modified_by", table, columnPrefix + "_modified_by"));
        columns.add(Column.aliased("modified_on", table, columnPrefix + "_modified_on"));

        return columns;
    }
}
