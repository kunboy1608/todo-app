package com.hoangdp.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ConversationsSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("conversation_id", table, columnPrefix + "_conversation_id"));
        columns.add(Column.aliased("timestamp", table, columnPrefix + "_timestamp"));
        columns.add(Column.aliased("sender", table, columnPrefix + "_sender"));
        columns.add(Column.aliased("receiver", table, columnPrefix + "_receiver"));
        columns.add(Column.aliased("message", table, columnPrefix + "_message"));

        return columns;
    }
}
