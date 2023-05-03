package com.hoangdp.gateway.repository.rowmapper;

import com.hoangdp.gateway.domain.Types;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Types}, with proper type conversions.
 */
@Service
public class TypesRowMapper implements BiFunction<Row, String, Types> {

    private final ColumnConverter converter;

    public TypesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Types} stored in the database.
     */
    @Override
    public Types apply(Row row, String prefix) {
        Types entity = new Types();
        entity.setTypeId(converter.fromRow(row, prefix + "_type_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setOwner(converter.fromRow(row, prefix + "_owner", Long.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setCreatedOn(converter.fromRow(row, prefix + "_created_on", Instant.class));
        entity.setModifiedBy(converter.fromRow(row, prefix + "_modified_by", String.class));
        entity.setModifiedOn(converter.fromRow(row, prefix + "_modified_on", Instant.class));
        entity.setProfilesId(converter.fromRow(row, prefix + "_profiles_profile_id", Long.class));
        return entity;
    }
}
