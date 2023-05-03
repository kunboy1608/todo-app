package com.hoangdp.gateway.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.hoangdp.gateway.domain.DebtLoan;
import com.hoangdp.gateway.repository.rowmapper.DebtLoanRowMapper;
import com.hoangdp.gateway.repository.rowmapper.ProfilesRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the DebtLoan entity.
 */
@SuppressWarnings("unused")
class DebtLoanRepositoryInternalImpl extends SimpleR2dbcRepository<DebtLoan, Long> implements DebtLoanRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProfilesRowMapper profilesMapper;
    private final DebtLoanRowMapper debtloanMapper;

    private static final Table entityTable = Table.aliased("debt_loan", EntityManager.ENTITY_ALIAS);
    private static final Table debtsTable = Table.aliased("profiles", "debts");
    private static final Table loansTable = Table.aliased("profiles", "loans");

    public DebtLoanRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProfilesRowMapper profilesMapper,
        DebtLoanRowMapper debtloanMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(DebtLoan.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.profilesMapper = profilesMapper;
        this.debtloanMapper = debtloanMapper;
    }

    @Override
    public Flux<DebtLoan> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<DebtLoan> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = DebtLoanSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProfilesSqlHelper.getColumns(debtsTable, "debts"));
        columns.addAll(ProfilesSqlHelper.getColumns(loansTable, "loans"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(debtsTable)
            .on(Column.create("debts_profile_id", entityTable))
            .equals(Column.create("profile_id", debtsTable))
            .leftOuterJoin(loansTable)
            .on(Column.create("loans_profile_id", entityTable))
            .equals(Column.create("profile_id", loansTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, DebtLoan.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<DebtLoan> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<DebtLoan> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private DebtLoan process(Row row, RowMetadata metadata) {
        DebtLoan entity = debtloanMapper.apply(row, "e");
        entity.setDebts(profilesMapper.apply(row, "debts"));
        entity.setLoans(profilesMapper.apply(row, "loans"));
        return entity;
    }

    @Override
    public <S extends DebtLoan> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
