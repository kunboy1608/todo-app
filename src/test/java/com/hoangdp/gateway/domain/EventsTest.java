package com.hoangdp.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hoangdp.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Events.class);
        Events events1 = new Events();
        events1.setEventId(1L);
        Events events2 = new Events();
        events2.setEventId(events1.getEventId());
        assertThat(events1).isEqualTo(events2);
        events2.setEventId(2L);
        assertThat(events1).isNotEqualTo(events2);
        events1.setEventId(null);
        assertThat(events1).isNotEqualTo(events2);
    }
}
