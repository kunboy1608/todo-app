package com.hoangdp.gateway.repository.rowmapper;

import com.hoangdp.gateway.domain.Relationship;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Relationship}, with proper type conversions.
 */
@Service
public class RelationshipRowMapper implements BiFunction<Row, String, Relationship> {

    private final ColumnConverter converter;

    public RelationshipRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Relationship} stored in the database.
     */
    @Override
    public Relationship apply(Row row, String prefix) {
        Relationship entity = new Relationship();
        entity.setRelationshipId(converter.fromRow(row, prefix + "_relationship_id", Long.class));
        entity.setOwner(converter.fromRow(row, prefix + "_owner", Long.class));
        entity.setPartner(converter.fromRow(row, prefix + "_partner", Long.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", Integer.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setCreatedOn(converter.fromRow(row, prefix + "_created_on", Instant.class));
        entity.setModifiedBy(converter.fromRow(row, prefix + "_modified_by", String.class));
        entity.setModifiedOn(converter.fromRow(row, prefix + "_modified_on", Instant.class));
        return entity;
    }
}
