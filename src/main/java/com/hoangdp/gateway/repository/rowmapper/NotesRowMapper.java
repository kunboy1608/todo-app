package com.hoangdp.gateway.repository.rowmapper;

import com.hoangdp.gateway.domain.Notes;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Notes}, with proper type conversions.
 */
@Service
public class NotesRowMapper implements BiFunction<Row, String, Notes> {

    private final ColumnConverter converter;

    public NotesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Notes} stored in the database.
     */
    @Override
    public Notes apply(Row row, String prefix) {
        Notes entity = new Notes();
        entity.setNoteId(converter.fromRow(row, prefix + "_note_id", Long.class));
        entity.setOwner(converter.fromRow(row, prefix + "_owner", Long.class));
        entity.setContent(converter.fromRow(row, prefix + "_content", String.class));
        entity.setProfilesId(converter.fromRow(row, prefix + "_profiles_profile_id", Long.class));
        return entity;
    }
}
