package com.hoangdp.gateway.repository.rowmapper;

import com.hoangdp.gateway.domain.ConversationsDetails;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ConversationsDetails}, with proper type conversions.
 */
@Service
public class ConversationsDetailsRowMapper implements BiFunction<Row, String, ConversationsDetails> {

    private final ColumnConverter converter;

    public ConversationsDetailsRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ConversationsDetails} stored in the database.
     */
    @Override
    public ConversationsDetails apply(Row row, String prefix) {
        ConversationsDetails entity = new ConversationsDetails();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setIsGroup(converter.fromRow(row, prefix + "_is_group", Boolean.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setCreatedOn(converter.fromRow(row, prefix + "_created_on", Instant.class));
        entity.setModifiedBy(converter.fromRow(row, prefix + "_modified_by", String.class));
        entity.setModifiedOn(converter.fromRow(row, prefix + "_modified_on", Instant.class));
        entity.setConversationsId(converter.fromRow(row, prefix + "_conversations_conversation_id", Long.class));
        return entity;
    }
}
