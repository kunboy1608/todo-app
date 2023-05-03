package com.hoangdp.gateway.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Events.
 */
@Table("events")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Events implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("event_id")
    private Long eventId;

    @Column("owner")
    private Long owner;

    @Column("kind")
    private Integer kind;

    @Column("date")
    private String date;

    @Column("is_lunar")
    private Boolean isLunar;

    @Transient
    @JsonIgnoreProperties(value = { "types", "expenses", "notes", "events", "tags" }, allowSetters = true)
    private Profiles profiles;

    @Column("profiles_profile_id")
    private Long profilesId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getEventId() {
        return this.eventId;
    }

    public Events eventId(Long eventId) {
        this.setEventId(eventId);
        return this;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getOwner() {
        return this.owner;
    }

    public Events owner(Long owner) {
        this.setOwner(owner);
        return this;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public Integer getKind() {
        return this.kind;
    }

    public Events kind(Integer kind) {
        this.setKind(kind);
        return this;
    }

    public void setKind(Integer kind) {
        this.kind = kind;
    }

    public String getDate() {
        return this.date;
    }

    public Events date(String date) {
        this.setDate(date);
        return this;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getIsLunar() {
        return this.isLunar;
    }

    public Events isLunar(Boolean isLunar) {
        this.setIsLunar(isLunar);
        return this;
    }

    public void setIsLunar(Boolean isLunar) {
        this.isLunar = isLunar;
    }

    public Profiles getProfiles() {
        return this.profiles;
    }

    public void setProfiles(Profiles profiles) {
        this.profiles = profiles;
        this.profilesId = profiles != null ? profiles.getProfileId() : null;
    }

    public Events profiles(Profiles profiles) {
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
        if (!(o instanceof Events)) {
            return false;
        }
        return eventId != null && eventId.equals(((Events) o).eventId);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Events{" +
            "eventId=" + getEventId() +
            ", owner=" + getOwner() +
            ", kind=" + getKind() +
            ", date='" + getDate() + "'" +
            ", isLunar='" + getIsLunar() + "'" +
            "}";
    }
}
