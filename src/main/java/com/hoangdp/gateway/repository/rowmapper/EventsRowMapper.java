package com.hoangdp.gateway.repository.rowmapper;

import com.hoangdp.gateway.domain.Events;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Events}, with proper type conversions.
 */
@Service
public class EventsRowMapper implements BiFunction<Row, String, Events> {

    private final ColumnConverter converter;

    public EventsRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Events} stored in the database.
     */
    @Override
    public Events apply(Row row, String prefix) {
        Events entity = new Events();
        entity.setEventId(converter.fromRow(row, prefix + "_event_id", Long.class));
        entity.setOwner(converter.fromRow(row, prefix + "_owner", Long.class));
        entity.setKind(converter.fromRow(row, prefix + "_kind", Integer.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", String.class));
        entity.setIsLunar(converter.fromRow(row, prefix + "_is_lunar", Boolean.class));
        entity.setProfilesId(converter.fromRow(row, prefix + "_profiles_profile_id", Long.class));
        return entity;
    }
}
