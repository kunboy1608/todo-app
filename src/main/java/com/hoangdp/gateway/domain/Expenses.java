package com.hoangdp.gateway.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Expenses.
 */
@Table("expenses")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Expenses implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("expense_id")
    private Long expenseId;

    @Column("owner")
    private Long owner;

    @Column("content")
    private String content;

    @Column("cost")
    private Double cost;

    @Column("tag")
    private String tag;

    @Column("day")
    private LocalDate day;

    @Column("created_by")
    private String createdBy;

    @Column("created_on")
    private Instant createdOn;

    @Column("modified_by")
    private String modifiedBy;

    @Column("modified_on")
    private Instant modifiedOn;

    @Transient
    @JsonIgnoreProperties(value = { "expenses", "profiles" }, allowSetters = true)
    private Types types;

    @Transient
    @JsonIgnoreProperties(value = { "types", "expenses", "notes", "events", "tags" }, allowSetters = true)
    private Profiles profiles;

    @Column("types_type_id")
    private Long typesId;

    @Column("profiles_profile_id")
    private Long profilesId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getExpenseId() {
        return this.expenseId;
    }

    public Expenses expenseId(Long expenseId) {
        this.setExpenseId(expenseId);
        return this;
    }

    public void setExpenseId(Long expenseId) {
        this.expenseId = expenseId;
    }

    public Long getOwner() {
        return this.owner;
    }

    public Expenses owner(Long owner) {
        this.setOwner(owner);
        return this;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public String getContent() {
        return this.content;
    }

    public Expenses content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getCost() {
        return this.cost;
    }

    public Expenses cost(Double cost) {
        this.setCost(cost);
        return this;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getTag() {
        return this.tag;
    }

    public Expenses tag(String tag) {
        this.setTag(tag);
        return this;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public LocalDate getDay() {
        return this.day;
    }

    public Expenses day(LocalDate day) {
        this.setDay(day);
        return this;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Expenses createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedOn() {
        return this.createdOn;
    }

    public Expenses createdOn(Instant createdOn) {
        this.setCreatedOn(createdOn);
        return this;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public Expenses modifiedBy(String modifiedBy) {
        this.setModifiedBy(modifiedBy);
        return this;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Instant getModifiedOn() {
        return this.modifiedOn;
    }

    public Expenses modifiedOn(Instant modifiedOn) {
        this.setModifiedOn(modifiedOn);
        return this;
    }

    public void setModifiedOn(Instant modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Types getTypes() {
        return this.types;
    }

    public void setTypes(Types types) {
        this.types = types;
        this.typesId = types != null ? types.getTypeId() : null;
    }

    public Expenses types(Types types) {
        this.setTypes(types);
        return this;
    }

    public Profiles getProfiles() {
        return this.profiles;
    }

    public void setProfiles(Profiles profiles) {
        this.profiles = profiles;
        this.profilesId = profiles != null ? profiles.getProfileId() : null;
    }

    public Expenses profiles(Profiles profiles) {
        this.setProfiles(profiles);
        return this;
    }

    public Long getTypesId() {
        return this.typesId;
    }

    public void setTypesId(Long types) {
        this.typesId = types;
    }

    public Long getProfilesId() {
        return this.profilesId;
    }

    public void setProfilesId(Long profiles) {
        this.profilesId = profiles;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Expenses)) {
            return false;
        }
        return expenseId != null && expenseId.equals(((Expenses) o).expenseId);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Expenses{" +
            "expenseId=" + getExpenseId() +
            ", owner=" + getOwner() +
            ", content='" + getContent() + "'" +
            ", cost=" + getCost() +
            ", tag='" + getTag() + "'" +
            ", day='" + getDay() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdOn='" + getCreatedOn() + "'" +
            ", modifiedBy='" + getModifiedBy() + "'" +
            ", modifiedOn='" + getModifiedOn() + "'" +
            "}";
    }
}
