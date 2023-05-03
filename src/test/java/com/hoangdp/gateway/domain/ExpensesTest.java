package com.hoangdp.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hoangdp.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExpensesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Expenses.class);
        Expenses expenses1 = new Expenses();
        expenses1.setExpenseId(1L);
        Expenses expenses2 = new Expenses();
        expenses2.setExpenseId(expenses1.getExpenseId());
        assertThat(expenses1).isEqualTo(expenses2);
        expenses2.setExpenseId(2L);
        assertThat(expenses1).isNotEqualTo(expenses2);
        expenses1.setExpenseId(null);
        assertThat(expenses1).isNotEqualTo(expenses2);
    }
}
