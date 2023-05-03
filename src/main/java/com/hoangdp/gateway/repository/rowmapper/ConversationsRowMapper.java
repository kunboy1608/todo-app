package com.hoangdp.gateway.repository.rowmapper;

import com.hoangdp.gateway.domain.Conversations;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Conversations}, with proper type conversions.
 */
@Service
public class ConversationsRowMapper implements BiFunction<Row, String, Conversations> {

    private final ColumnConverter converter;

    public ConversationsRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Conversations} stored in the database.
     */
    @Override
    public Conversations apply(Row row, String prefix) {
        Conversations entity = new Conversations();
        entity.setConversationId(converter.fromRow(row, prefix + "_conversation_id", Long.class));
        entity.setTimestamp(converter.fromRow(row, prefix + "_timestamp", Instant.class));
        entity.setSender(converter.fromRow(row, prefix + "_sender", Long.class));
        entity.setReceiver(converter.fromRow(row, prefix + "_receiver", Long.class));
        entity.setMessage(converter.fromRow(row, prefix + "_message", String.class));
        return entity;
    }
}
