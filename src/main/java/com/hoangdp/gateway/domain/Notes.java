package com.hoangdp.gateway.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Notes.
 */
@Table("notes")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Notes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("note_id")
    private Long noteId;

    @Column("owner")
    private Long owner;

    @Column("content")
    private String content;

    @Transient
    @JsonIgnoreProperties(value = { "types", "expenses", "notes", "events", "tags" }, allowSetters = true)
    private Profiles profiles;

    @Column("profiles_profile_id")
    private Long profilesId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getNoteId() {
        return this.noteId;
    }

    public Notes noteId(Long noteId) {
        this.setNoteId(noteId);
        return this;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public Long getOwner() {
        return this.owner;
    }

    public Notes owner(Long owner) {
        this.setOwner(owner);
        return this;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public String getContent() {
        return this.content;
    }

    public Notes content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Profiles getProfiles() {
        return this.profiles;
    }

    public void setProfiles(Profiles profiles) {
        this.profiles = profiles;
        this.profilesId = profiles != null ? profiles.getProfileId() : null;
    }

    public Notes profiles(Profiles profiles) {
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
        if (!(o instanceof Notes)) {
            return false;
        }
        return noteId != null && noteId.equals(((Notes) o).noteId);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notes{" +
            "noteId=" + getNoteId() +
            ", owner=" + getOwner() +
            ", content='" + getContent() + "'" +
            "}";
    }
}
