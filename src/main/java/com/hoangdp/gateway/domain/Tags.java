package com.hoangdp.gateway.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Tags.
 */
@Table("tags")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tags implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("tag_id")
    private Long tagId;

    @Column("owner")
    private Long owner;

    @Column("name")
    private String name;

    @Transient
    @JsonIgnoreProperties(value = { "types", "expenses", "notes", "events", "tags" }, allowSetters = true)
    private Profiles profiles;

    @Column("profiles_profile_id")
    private Long profilesId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getTagId() {
        return this.tagId;
    }

    public Tags tagId(Long tagId) {
        this.setTagId(tagId);
        return this;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Long getOwner() {
        return this.owner;
    }

    public Tags owner(Long owner) {
        this.setOwner(owner);
        return this;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public String getName() {
        return this.name;
    }

    public Tags name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Profiles getProfiles() {
        return this.profiles;
    }

    public void setProfiles(Profiles profiles) {
        this.profiles = profiles;
        this.profilesId = profiles != null ? profiles.getProfileId() : null;
    }

    public Tags profiles(Profiles profiles) {
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
        if (!(o instanceof Tags)) {
            return false;
        }
        return tagId != null && tagId.equals(((Tags) o).tagId);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tags{" +
            "tagId=" + getTagId() +
            ", owner=" + getOwner() +
            ", name='" + getName() + "'" +
            "}";
    }
}
