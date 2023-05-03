package com.hoangdp.gateway.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.hoangdp.gateway.domain.Tags;
import com.hoangdp.gateway.repository.rowmapper.ProfilesRowMapper;
import com.hoangdp.gateway.repository.rowmapper.TagsRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
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
 * Spring Data R2DBC custom repository implementation for the Tags entity.
 */
@SuppressWarnings("unused")
class TagsRepositoryInternalImpl extends SimpleR2dbcRepository<Tags, Long> implements TagsRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProfilesRowMapper profilesMapper;
    private final TagsRowMapper tagsMapper;

    private static final Table entityTable = Table.aliased("tags", EntityManager.ENTITY_ALIAS);
    private static final Table profilesTable = Table.aliased("profiles", "profiles");

    public TagsRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProfilesRowMapper profilesMapper,
        TagsRowMapper tagsMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Tags.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.profilesMapper = profilesMapper;
        this.tagsMapper = tagsMapper;
    }

    @Override
    public Flux<Tags> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Tags> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TagsSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProfilesSqlHelper.getColumns(profilesTable, "profiles"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(profilesTable)
            .on(Column.create("profiles_profile_id", entityTable))
            .equals(Column.create("profile_id", profilesTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Tags.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Tags> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Tags> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("tag_id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Tags process(Row row, RowMetadata metadata) {
        Tags entity = tagsMapper.apply(row, "e");
        entity.setProfiles(profilesMapper.apply(row, "profiles"));
        return entity;
    }

    @Override
    public <S extends Tags> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
