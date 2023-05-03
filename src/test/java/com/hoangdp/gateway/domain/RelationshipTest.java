package com.hoangdp.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hoangdp.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RelationshipTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Relationship.class);
        Relationship relationship1 = new Relationship();
        relationship1.setRelationshipId(1L);
        Relationship relationship2 = new Relationship();
        relationship2.setRelationshipId(relationship1.getRelationshipId());
        assertThat(relationship1).isEqualTo(relationship2);
        relationship2.setRelationshipId(2L);
        assertThat(relationship1).isNotEqualTo(relationship2);
        relationship1.setRelationshipId(null);
        assertThat(relationship1).isNotEqualTo(relationship2);
    }
}
