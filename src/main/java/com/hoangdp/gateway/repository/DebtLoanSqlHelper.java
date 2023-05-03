package com.hoangdp.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class DebtLoanSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("loan_user_id", table, columnPrefix + "_loan_user_id"));
        columns.add(Column.aliased("debt_user_id", table, columnPrefix + "_debt_user_id"));
        columns.add(Column.aliased("cost", table, columnPrefix + "_cost"));
        columns.add(Column.aliased("deadline", table, columnPrefix + "_deadline"));
        columns.add(Column.aliased("dat_of_payment", table, columnPrefix + "_dat_of_payment"));

        columns.add(Column.aliased("debts_profile_id", table, columnPrefix + "_debts_profile_id"));
        columns.add(Column.aliased("loans_profile_id", table, columnPrefix + "_loans_profile_id"));
        return columns;
    }
}
