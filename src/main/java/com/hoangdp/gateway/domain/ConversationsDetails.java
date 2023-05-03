package com.hoangdp.gateway.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ConversationsDetails.
 */
@Table("conversations_details")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ConversationsDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("is_group")
    private Boolean isGroup;

    @Column("created_by")
    private String createdBy;

    @Column("created_on")
    private Instant createdOn;

    @Column("modified_by")
    private String modifiedBy;

    @Column("modified_on")
    private Instant modifiedOn;

    @Transient
    @JsonIgnoreProperties(value = { "conversationsDetails" }, allowSetters = true)
    private Conversations conversations;

    @Column("conversations_conversation_id")
    private Long conversationsId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ConversationsDetails id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ConversationsDetails name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsGroup() {
        return this.isGroup;
    }

    public ConversationsDetails isGroup(Boolean isGroup) {
        this.setIsGroup(isGroup);
        return this;
    }

    public void setIsGroup(Boolean isGroup) {
        this.isGroup = isGroup;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public ConversationsDetails createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedOn() {
        return this.createdOn;
    }

    public ConversationsDetails createdOn(Instant createdOn) {
        this.setCreatedOn(createdOn);
        return this;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public ConversationsDetails modifiedBy(String modifiedBy) {
        this.setModifiedBy(modifiedBy);
        return this;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Instant getModifiedOn() {
        return this.modifiedOn;
    }

    public ConversationsDetails modifiedOn(Instant modifiedOn) {
        this.setModifiedOn(modifiedOn);
        return this;
    }

    public void setModifiedOn(Instant modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Conversations getConversations() {
        return this.conversations;
    }

    public void setConversations(Conversations conversations) {
        this.conversations = conversations;
        this.conversationsId = conversations != null ? conversations.getConversationId() : null;
    }

    public ConversationsDetails conversations(Conversations conversations) {
        this.setConversations(conversations);
        return this;
    }

    public Long getConversationsId() {
        return this.conversationsId;
    }

    public void setConversationsId(Long conversations) {
        this.conversationsId = conversations;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConversationsDetails)) {
            return false;
        }
        return id != null && id.equals(((ConversationsDetails) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ConversationsDetails{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", isGroup='" + getIsGroup() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdOn='" + getCreatedOn() + "'" +
            ", modifiedBy='" + getModifiedBy() + "'" +
            ", modifiedOn='" + getModifiedOn() + "'" +
            "}";
    }
}
