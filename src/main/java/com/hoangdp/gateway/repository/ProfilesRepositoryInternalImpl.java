package com.hoangdp.gateway.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.hoangdp.gateway.domain.Profiles;
import com.hoangdp.gateway.repository.rowmapper.ProfilesRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.time.LocalDate;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Profiles entity.
 */
@SuppressWarnings("unused")
class ProfilesRepositoryInternalImpl extends SimpleR2dbcRepository<Profiles, Long> implements ProfilesRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProfilesRowMapper profilesMapper;

    private static final Table entityTable = Table.aliased("profiles", EntityManager.ENTITY_ALIAS);

    public ProfilesRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProfilesRowMapper profilesMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Profiles.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.profilesMapper = profilesMapper;
    }

    @Override
    public Flux<Profiles> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Profiles> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProfilesSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Profiles.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Profiles> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Profiles> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("profile_id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Profiles process(Row row, RowMetadata metadata) {
        Profiles entity = profilesMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Profiles> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
