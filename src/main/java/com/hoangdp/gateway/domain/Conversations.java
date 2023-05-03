package com.hoangdp.gateway.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Conversations.
 */
@Table("conversations")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Conversations implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("conversation_id")
    private Long conversationId;

    @NotNull(message = "must not be null")
    @Column("timestamp")
    private Instant timestamp;

    @NotNull(message = "must not be null")
    @Column("sender")
    private Long sender;

    @NotNull(message = "must not be null")
    @Column("receiver")
    private Long receiver;

    @NotNull(message = "must not be null")
    @Column("message")
    private String message;

    @Transient
    @JsonIgnoreProperties(value = { "conversations" }, allowSetters = true)
    private Set<ConversationsDetails> conversationsDetails = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getConversationId() {
        return this.conversationId;
    }

    public Conversations conversationId(Long conversationId) {
        this.setConversationId(conversationId);
        return this;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public Conversations timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Long getSender() {
        return this.sender;
    }

    public Conversations sender(Long sender) {
        this.setSender(sender);
        return this;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Long getReceiver() {
        return this.receiver;
    }

    public Conversations receiver(Long receiver) {
        this.setReceiver(receiver);
        return this;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return this.message;
    }

    public Conversations message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<ConversationsDetails> getConversationsDetails() {
        return this.conversationsDetails;
    }

    public void setConversationsDetails(Set<ConversationsDetails> conversationsDetails) {
        if (this.conversationsDetails != null) {
            this.conversationsDetails.forEach(i -> i.setConversations(null));
        }
        if (conversationsDetails != null) {
            conversationsDetails.forEach(i -> i.setConversations(this));
        }
        this.conversationsDetails = conversationsDetails;
    }

    public Conversations conversationsDetails(Set<ConversationsDetails> conversationsDetails) {
        this.setConversationsDetails(conversationsDetails);
        return this;
    }

    public Conversations addConversationsDetails(ConversationsDetails conversationsDetails) {
        this.conversationsDetails.add(conversationsDetails);
        conversationsDetails.setConversations(this);
        return this;
    }

    public Conversations removeConversationsDetails(ConversationsDetails conversationsDetails) {
        this.conversationsDetails.remove(conversationsDetails);
        conversationsDetails.setConversations(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Conversations)) {
            return false;
        }
        return conversationId != null && conversationId.equals(((Conversations) o).conversationId);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Conversations{" +
            "conversationId=" + getConversationId() +
            ", timestamp='" + getTimestamp() + "'" +
            ", sender=" + getSender() +
            ", receiver=" + getReceiver() +
            ", message='" + getMessage() + "'" +
            "}";
    }
}
