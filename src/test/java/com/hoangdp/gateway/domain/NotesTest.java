package com.hoangdp.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hoangdp.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notes.class);
        Notes notes1 = new Notes();
        notes1.setNoteId(1L);
        Notes notes2 = new Notes();
        notes2.setNoteId(notes1.getNoteId());
        assertThat(notes1).isEqualTo(notes2);
        notes2.setNoteId(2L);
        assertThat(notes1).isNotEqualTo(notes2);
        notes1.setNoteId(null);
        assertThat(notes1).isNotEqualTo(notes2);
    }
}
