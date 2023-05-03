package com.hoangdp.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hoangdp.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DebtLoanTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DebtLoan.class);
        DebtLoan debtLoan1 = new DebtLoan();
        debtLoan1.setId(1L);
        DebtLoan debtLoan2 = new DebtLoan();
        debtLoan2.setId(debtLoan1.getId());
        assertThat(debtLoan1).isEqualTo(debtLoan2);
        debtLoan2.setId(2L);
        assertThat(debtLoan1).isNotEqualTo(debtLoan2);
        debtLoan1.setId(null);
        assertThat(debtLoan1).isNotEqualTo(debtLoan2);
    }
}
