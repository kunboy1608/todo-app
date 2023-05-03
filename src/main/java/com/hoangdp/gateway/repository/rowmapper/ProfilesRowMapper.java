package com.hoangdp.gateway.repository.rowmapper;

import com.hoangdp.gateway.domain.Profiles;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Profiles}, with proper type conversions.
 */
@Service
public class ProfilesRowMapper implements BiFunction<Row, String, Profiles> {

    private final ColumnConverter converter;

    public ProfilesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Profiles} stored in the database.
     */
    @Override
    public Profiles apply(Row row, String prefix) {
        Profiles entity = new Profiles();
        entity.setProfileId(converter.fromRow(row, prefix + "_profile_id", Long.class));
        entity.setUsername(converter.fromRow(row, prefix + "_username", String.class));
        entity.setNickname(converter.fromRow(row, prefix + "_nickname", String.class));
        entity.setBirthday(converter.fromRow(row, prefix + "_birthday", LocalDate.class));
        entity.setBio(converter.fromRow(row, prefix + "_bio", String.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setCreatedOn(converter.fromRow(row, prefix + "_created_on", Instant.class));
        entity.setModifiedBy(converter.fromRow(row, prefix + "_modified_by", String.class));
        entity.setModifiedOn(converter.fromRow(row, prefix + "_modified_on", Instant.class));
        return entity;
    }
}
