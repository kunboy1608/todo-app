package com.hoangdp.gateway.repository.rowmapper;

import com.hoangdp.gateway.domain.Tags;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Tags}, with proper type conversions.
 */
@Service
public class TagsRowMapper implements BiFunction<Row, String, Tags> {

    private final ColumnConverter converter;

    public TagsRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Tags} stored in the database.
     */
    @Override
    public Tags apply(Row row, String prefix) {
        Tags entity = new Tags();
        entity.setTagId(converter.fromRow(row, prefix + "_tag_id", Long.class));
        entity.setOwner(converter.fromRow(row, prefix + "_owner", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setProfilesId(converter.fromRow(row, prefix + "_profiles_profile_id", Long.class));
        return entity;
    }
}
