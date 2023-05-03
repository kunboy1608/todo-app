package com.hoangdp.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ConversationsDetailsSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("is_group", table, columnPrefix + "_is_group"));
        columns.add(Column.aliased("created_by", table, columnPrefix + "_created_by"));
        columns.add(Column.aliased("created_on", table, columnPrefix + "_created_on"));
        columns.add(Column.aliased("modified_by", table, columnPrefix + "_modified_by"));
        columns.add(Column.aliased("modified_on", table, columnPrefix + "_modified_on"));

        columns.add(Column.aliased("conversations_conversation_id", table, columnPrefix + "_conversations_conversation_id"));
        return columns;
    }
}
