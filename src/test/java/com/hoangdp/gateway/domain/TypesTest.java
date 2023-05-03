package com.hoangdp.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hoangdp.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TypesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Types.class);
        Types types1 = new Types();
        types1.setTypeId(1L);
        Types types2 = new Types();
        types2.setTypeId(types1.getTypeId());
        assertThat(types1).isEqualTo(types2);
        types2.setTypeId(2L);
        assertThat(types1).isNotEqualTo(types2);
        types1.setTypeId(null);
        assertThat(types1).isNotEqualTo(types2);
    }
}
