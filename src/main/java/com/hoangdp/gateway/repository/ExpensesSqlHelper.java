package com.hoangdp.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ExpensesSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("expense_id", table, columnPrefix + "_expense_id"));
        columns.add(Column.aliased("owner", table, columnPrefix + "_owner"));
        columns.add(Column.aliased("content", table, columnPrefix + "_content"));
        columns.add(Column.aliased("cost", table, columnPrefix + "_cost"));
        columns.add(Column.aliased("tag", table, columnPrefix + "_tag"));
        columns.add(Column.aliased("day", table, columnPrefix + "_day"));
        columns.add(Column.aliased("created_by", table, columnPrefix + "_created_by"));
        columns.add(Column.aliased("created_on", table, columnPrefix + "_created_on"));
        columns.add(Column.aliased("modified_by", table, columnPrefix + "_modified_by"));
        columns.add(Column.aliased("modified_on", table, columnPrefix + "_modified_on"));

        columns.add(Column.aliased("types_type_id", table, columnPrefix + "_types_type_id"));
        columns.add(Column.aliased("profiles_profile_id", table, columnPrefix + "_profiles_profile_id"));
        return columns;
    }
}
