package com.hoangdp.gateway.repository.rowmapper;

import com.hoangdp.gateway.domain.Expenses;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Expenses}, with proper type conversions.
 */
@Service
public class ExpensesRowMapper implements BiFunction<Row, String, Expenses> {

    private final ColumnConverter converter;

    public ExpensesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Expenses} stored in the database.
     */
    @Override
    public Expenses apply(Row row, String prefix) {
        Expenses entity = new Expenses();
        entity.setExpenseId(converter.fromRow(row, prefix + "_expense_id", Long.class));
        entity.setOwner(converter.fromRow(row, prefix + "_owner", Long.class));
        entity.setContent(converter.fromRow(row, prefix + "_content", String.class));
        entity.setCost(converter.fromRow(row, prefix + "_cost", Double.class));
        entity.setTag(converter.fromRow(row, prefix + "_tag", String.class));
        entity.setDay(converter.fromRow(row, prefix + "_day", LocalDate.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setCreatedOn(converter.fromRow(row, prefix + "_created_on", Instant.class));
        entity.setModifiedBy(converter.fromRow(row, prefix + "_modified_by", String.class));
        entity.setModifiedOn(converter.fromRow(row, prefix + "_modified_on", Instant.class));
        entity.setTypesId(converter.fromRow(row, prefix + "_types_type_id", Long.class));
        entity.setProfilesId(converter.fromRow(row, prefix + "_profiles_profile_id", Long.class));
        return entity;
    }
}
