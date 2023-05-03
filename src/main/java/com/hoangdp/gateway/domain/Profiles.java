package com.hoangdp.gateway.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Profiles.
 */
@Table("profiles")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Profiles implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("profile_id")
    private Long profileId;

    @Column("username")
    private String username;

    @Column("nickname")
    private String nickname;

    @Column("birthday")
    private LocalDate birthday;

    @Column("bio")
    private String bio;

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
    private Set<Types> types = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "types", "profiles" }, allowSetters = true)
    private Set<Expenses> expenses = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "profiles" }, allowSetters = true)
    private Set<Notes> notes = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "profiles" }, allowSetters = true)
    private Set<Events> events = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "profiles" }, allowSetters = true)
    private Set<Tags> tags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getProfileId() {
        return this.profileId;
    }

    public Profiles profileId(Long profileId) {
        this.setProfileId(profileId);
        return this;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getUsername() {
        return this.username;
    }

    public Profiles username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return this.nickname;
    }

    public Profiles nickname(String nickname) {
        this.setNickname(nickname);
        return this;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LocalDate getBirthday() {
        return this.birthday;
    }

    public Profiles birthday(LocalDate birthday) {
        this.setBirthday(birthday);
        return this;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getBio() {
        return this.bio;
    }

    public Profiles bio(String bio) {
        this.setBio(bio);
        return this;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Profiles createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedOn() {
        return this.createdOn;
    }

    public Profiles createdOn(Instant createdOn) {
        this.setCreatedOn(createdOn);
        return this;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public Profiles modifiedBy(String modifiedBy) {
        this.setModifiedBy(modifiedBy);
        return this;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Instant getModifiedOn() {
        return this.modifiedOn;
    }

    public Profiles modifiedOn(Instant modifiedOn) {
        this.setModifiedOn(modifiedOn);
        return this;
    }

    public void setModifiedOn(Instant modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Set<Types> getTypes() {
        return this.types;
    }

    public void setTypes(Set<Types> types) {
        if (this.types != null) {
            this.types.forEach(i -> i.setProfiles(null));
        }
        if (types != null) {
            types.forEach(i -> i.setProfiles(this));
        }
        this.types = types;
    }

    public Profiles types(Set<Types> types) {
        this.setTypes(types);
        return this;
    }

    public Profiles addTypes(Types types) {
        this.types.add(types);
        types.setProfiles(this);
        return this;
    }

    public Profiles removeTypes(Types types) {
        this.types.remove(types);
        types.setProfiles(null);
        return this;
    }

    public Set<Expenses> getExpenses() {
        return this.expenses;
    }

    public void setExpenses(Set<Expenses> expenses) {
        if (this.expenses != null) {
            this.expenses.forEach(i -> i.setProfiles(null));
        }
        if (expenses != null) {
            expenses.forEach(i -> i.setProfiles(this));
        }
        this.expenses = expenses;
    }

    public Profiles expenses(Set<Expenses> expenses) {
        this.setExpenses(expenses);
        return this;
    }

    public Profiles addExpenses(Expenses expenses) {
        this.expenses.add(expenses);
        expenses.setProfiles(this);
        return this;
    }

    public Profiles removeExpenses(Expenses expenses) {
        this.expenses.remove(expenses);
        expenses.setProfiles(null);
        return this;
    }

    public Set<Notes> getNotes() {
        return this.notes;
    }

    public void setNotes(Set<Notes> notes) {
        if (this.notes != null) {
            this.notes.forEach(i -> i.setProfiles(null));
        }
        if (notes != null) {
            notes.forEach(i -> i.setProfiles(this));
        }
        this.notes = notes;
    }

    public Profiles notes(Set<Notes> notes) {
        this.setNotes(notes);
        return this;
    }

    public Profiles addNotes(Notes notes) {
        this.notes.add(notes);
        notes.setProfiles(this);
        return this;
    }

    public Profiles removeNotes(Notes notes) {
        this.notes.remove(notes);
        notes.setProfiles(null);
        return this;
    }

    public Set<Events> getEvents() {
        return this.events;
    }

    public void setEvents(Set<Events> events) {
        if (this.events != null) {
            this.events.forEach(i -> i.setProfiles(null));
        }
        if (events != null) {
            events.forEach(i -> i.setProfiles(this));
        }
        this.events = events;
    }

    public Profiles events(Set<Events> events) {
        this.setEvents(events);
        return this;
    }

    public Profiles addEvents(Events events) {
        this.events.add(events);
        events.setProfiles(this);
        return this;
    }

    public Profiles removeEvents(Events events) {
        this.events.remove(events);
        events.setProfiles(null);
        return this;
    }

    public Set<Tags> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tags> tags) {
        if (this.tags != null) {
            this.tags.forEach(i -> i.setProfiles(null));
        }
        if (tags != null) {
            tags.forEach(i -> i.setProfiles(this));
        }
        this.tags = tags;
    }

    public Profiles tags(Set<Tags> tags) {
        this.setTags(tags);
        return this;
    }

    public Profiles addTags(Tags tags) {
        this.tags.add(tags);
        tags.setProfiles(this);
        return this;
    }

    public Profiles removeTags(Tags tags) {
        this.tags.remove(tags);
        tags.setProfiles(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Profiles)) {
            return false;
        }
        return profileId != null && profileId.equals(((Profiles) o).profileId);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Profiles{" +
            "profileId=" + getProfileId() +
            ", username='" + getUsername() + "'" +
            ", nickname='" + getNickname() + "'" +
            ", birthday='" + getBirthday() + "'" +
            ", bio='" + getBio() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdOn='" + getCreatedOn() + "'" +
            ", modifiedBy='" + getModifiedBy() + "'" +
            ", modifiedOn='" + getModifiedOn() + "'" +
            "}";
    }
}
