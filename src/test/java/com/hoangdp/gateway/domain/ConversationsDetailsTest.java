package com.hoangdp.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hoangdp.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ConversationsDetailsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ConversationsDetails.class);
        ConversationsDetails conversationsDetails1 = new ConversationsDetails();
        conversationsDetails1.setId(1L);
        ConversationsDetails conversationsDetails2 = new ConversationsDetails();
        conversationsDetails2.setId(conversationsDetails1.getId());
        assertThat(conversationsDetails1).isEqualTo(conversationsDetails2);
        conversationsDetails2.setId(2L);
        assertThat(conversationsDetails1).isNotEqualTo(conversationsDetails2);
        conversationsDetails1.setId(null);
        assertThat(conversationsDetails1).isNotEqualTo(conversationsDetails2);
    }
}
