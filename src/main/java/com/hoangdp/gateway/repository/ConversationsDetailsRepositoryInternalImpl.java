package com.hoangdp.gateway.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.hoangdp.gateway.domain.ConversationsDetails;
import com.hoangdp.gateway.repository.rowmapper.ConversationsDetailsRowMapper;
import com.hoangdp.gateway.repository.rowmapper.ConversationsRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the ConversationsDetails entity.
 */
@SuppressWarnings("unused")
class ConversationsDetailsRepositoryInternalImpl
    extends SimpleR2dbcRepository<ConversationsDetails, Long>
    implements ConversationsDetailsRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ConversationsRowMapper conversationsMapper;
    private final ConversationsDetailsRowMapper conversationsdetailsMapper;

    private static final Table entityTable = Table.aliased("conversations_details", EntityManager.ENTITY_ALIAS);
    private static final Table conversationsTable = Table.aliased("conversations", "conversations");

    public ConversationsDetailsRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ConversationsRowMapper conversationsMapper,
        ConversationsDetailsRowMapper conversationsdetailsMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ConversationsDetails.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.conversationsMapper = conversationsMapper;
        this.conversationsdetailsMapper = conversationsdetailsMapper;
    }

    @Override
    public Flux<ConversationsDetails> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ConversationsDetails> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ConversationsDetailsSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ConversationsSqlHelper.getColumns(conversationsTable, "conversations"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(conversationsTable)
            .on(Column.create("conversations_conversation_id", entityTable))
            .equals(Column.create("conversation_id", conversationsTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ConversationsDetails.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ConversationsDetails> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ConversationsDetails> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private ConversationsDetails process(Row row, RowMetadata metadata) {
        ConversationsDetails entity = conversationsdetailsMapper.apply(row, "e");
        entity.setConversations(conversationsMapper.apply(row, "conversations"));
        return entity;
    }

    @Override
    public <S extends ConversationsDetails> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
