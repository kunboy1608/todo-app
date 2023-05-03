package com.hoangdp.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hoangdp.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ConversationsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Conversations.class);
        Conversations conversations1 = new Conversations();
        conversations1.setConversationId(1L);
        Conversations conversations2 = new Conversations();
        conversations2.setConversationId(conversations1.getConversationId());
        assertThat(conversations1).isEqualTo(conversations2);
        conversations2.setConversationId(2L);
        assertThat(conversations1).isNotEqualTo(conversations2);
        conversations1.setConversationId(null);
        assertThat(conversations1).isNotEqualTo(conversations2);
    }
}
