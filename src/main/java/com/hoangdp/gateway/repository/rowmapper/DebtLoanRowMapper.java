package com.hoangdp.gateway.repository.rowmapper;

import com.hoangdp.gateway.domain.DebtLoan;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link DebtLoan}, with proper type conversions.
 */
@Service
public class DebtLoanRowMapper implements BiFunction<Row, String, DebtLoan> {

    private final ColumnConverter converter;

    public DebtLoanRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link DebtLoan} stored in the database.
     */
    @Override
    public DebtLoan apply(Row row, String prefix) {
        DebtLoan entity = new DebtLoan();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLoanUserId(converter.fromRow(row, prefix + "_loan_user_id", Long.class));
        entity.setDebtUserId(converter.fromRow(row, prefix + "_debt_user_id", Long.class));
        entity.setCost(converter.fromRow(row, prefix + "_cost", Double.class));
        entity.setDeadline(converter.fromRow(row, prefix + "_deadline", Instant.class));
        entity.setDatOfPayment(converter.fromRow(row, prefix + "_dat_of_payment", Instant.class));
        entity.setDebtsId(converter.fromRow(row, prefix + "_debts_profile_id", Long.class));
        entity.setLoansId(converter.fromRow(row, prefix + "_loans_profile_id", Long.class));
        return entity;
    }
}
