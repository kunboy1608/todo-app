package com.hoangdp.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hoangdp.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TagsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tags.class);
        Tags tags1 = new Tags();
        tags1.setTagId(1L);
        Tags tags2 = new Tags();
        tags2.setTagId(tags1.getTagId());
        assertThat(tags1).isEqualTo(tags2);
        tags2.setTagId(2L);
        assertThat(tags1).isNotEqualTo(tags2);
        tags1.setTagId(null);
        assertThat(tags1).isNotEqualTo(tags2);
    }
}
