package com.hoangdp.gateway.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Types.
 */
@Table("types")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Types implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("type_id")
    private Long typeId;

    @Column("name")
    private String name;

    @Column("owner")
    private Long owner;

    @Column("created_by")
    private String createdBy;

    @Column("created_on")
    private Instant createdOn;

    @Column("modified_by")
    private String modifiedBy;

    @Column("modified_on")
    private Instant modifiedOn;

    @Transient
    @JsonIgnoreProperties(value = { "types", "profiles" }, allowSetters = true)
    private Set<Expenses> expenses = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "types", "expenses", "notes", "events", "tags" }, allowSetters = true)
    private Profiles profiles;

    @Column("profiles_profile_id")
    private Long profilesId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getTypeId() {
        return this.typeId;
    }

    public Types typeId(Long typeId) {
        this.setTypeId(typeId);
        return this;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return this.name;
    }

    public Types name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwner() {
        return this.owner;
    }

    public Types owner(Long owner) {
        this.setOwner(owner);
        return this;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Types createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedOn() {
        return this.createdOn;
    }

    public Types createdOn(Instant createdOn) {
        this.setCreatedOn(createdOn);
        return this;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public Types modifiedBy(String modifiedBy) {
        this.setModifiedBy(modifiedBy);
        return this;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Instant getModifiedOn() {
        return this.modifiedOn;
    }

    public Types modifiedOn(Instant modifiedOn) {
        this.setModifiedOn(modifiedOn);
        return this;
    }

    public void setModifiedOn(Instant modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Set<Expenses> getExpenses() {
        return this.expenses;
    }

    public void setExpenses(Set<Expenses> expenses) {
        if (this.expenses != null) {
            this.expenses.forEach(i -> i.setTypes(null));
        }
        if (expenses != null) {
            expenses.forEach(i -> i.setTypes(this));
        }
        this.expenses = expenses;
    }

    public Types expenses(Set<Expenses> expenses) {
        this.setExpenses(expenses);
        return this;
    }

    public Types addExpenses(Expenses expenses) {
        this.expenses.add(expenses);
        expenses.setTypes(this);
        return this;
    }

    public Types removeExpenses(Expenses expenses) {
        this.expenses.remove(expenses);
        expenses.setTypes(null);
        return this;
    }

    public Profiles getProfiles() {
        return this.profiles;
    }

    public void setProfiles(Profiles profiles) {
        this.profiles = profiles;
        this.profilesId = profiles != null ? profiles.getProfileId() : null;
    }

    public Types profiles(Profiles profiles) {
        this.setProfiles(profiles);
        return this;
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
        if (!(o instanceof Types)) {
            return false;
        }
        return typeId != null && typeId.equals(((Types) o).typeId);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Types{" +
            "typeId=" + getTypeId() +
            ", name='" + getName() + "'" +
            ", owner=" + getOwner() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdOn='" + getCreatedOn() + "'" +
            ", modifiedBy='" + getModifiedBy() + "'" +
            ", modifiedOn='" + getModifiedOn() + "'" +
            "}";
    }
}
