package com.hoangdp.gateway.domain;

import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Relationship.
 */
@Table("relationship")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Relationship implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("relationship_id")
    private Long relationshipId;

    @Column("owner")
    private Long owner;

    @Column("partner")
    private Long partner;

    @Column("status")
    private Integer status;

    @Column("created_by")
    private String createdBy;

    @Column("created_on")
    private Instant createdOn;

    @Column("modified_by")
    private String modifiedBy;

    @Column("modified_on")
    private Instant modifiedOn;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getRelationshipId() {
        return this.relationshipId;
    }

    public Relationship relationshipId(Long relationshipId) {
        this.setRelationshipId(relationshipId);
        return this;
    }

    public void setRelationshipId(Long relationshipId) {
        this.relationshipId = relationshipId;
    }

    public Long getOwner() {
        return this.owner;
    }

    public Relationship owner(Long owner) {
        this.setOwner(owner);
        return this;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public Long getPartner() {
        return this.partner;
    }

    public Relationship partner(Long partner) {
        this.setPartner(partner);
        return this;
    }

    public void setPartner(Long partner) {
        this.partner = partner;
    }

    public Integer getStatus() {
        return this.status;
    }

    public Relationship status(Integer status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Relationship createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedOn() {
        return this.createdOn;
    }

    public Relationship createdOn(Instant createdOn) {
        this.setCreatedOn(createdOn);
        return this;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public Relationship modifiedBy(String modifiedBy) {
        this.setModifiedBy(modifiedBy);
        return this;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Instant getModifiedOn() {
        return this.modifiedOn;
    }

    public Relationship modifiedOn(Instant modifiedOn) {
        this.setModifiedOn(modifiedOn);
        return this;
    }

    public void setModifiedOn(Instant modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Relationship)) {
            return false;
        }
        return relationshipId != null && relationshipId.equals(((Relationship) o).relationshipId);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Relationship{" +
            "relationshipId=" + getRelationshipId() +
            ", owner=" + getOwner() +
            ", partner=" + getPartner() +
            ", status=" + getStatus() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdOn='" + getCreatedOn() + "'" +
            ", modifiedBy='" + getModifiedBy() + "'" +
            ", modifiedOn='" + getModifiedOn() + "'" +
            "}";
    }
}
