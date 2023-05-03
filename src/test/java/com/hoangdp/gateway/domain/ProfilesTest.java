package com.hoangdp.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hoangdp.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfilesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Profiles.class);
        Profiles profiles1 = new Profiles();
        profiles1.setProfileId(1L);
        Profiles profiles2 = new Profiles();
        profiles2.setProfileId(profiles1.getProfileId());
        assertThat(profiles1).isEqualTo(profiles2);
        profiles2.setProfileId(2L);
        assertThat(profiles1).isNotEqualTo(profiles2);
        profiles1.setProfileId(null);
        assertThat(profiles1).isNotEqualTo(profiles2);
    }
}
