package com.hoangdp.gateway.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A DebtLoan.
 */
@Table("debt_loan")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DebtLoan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("loan_user_id")
    private Long loanUserId;

    @Column("debt_user_id")
    private Long debtUserId;

    @Column("cost")
    private Double cost;

    @Column("deadline")
    private Instant deadline;

    @Column("dat_of_payment")
    private Instant datOfPayment;

    @Transient
    private Profiles debts;

    @Transient
    private Profiles loans;

    @Column("debts_profile_id")
    private Long debtsId;

    @Column("loans_profile_id")
    private Long loansId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DebtLoan id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoanUserId() {
        return this.loanUserId;
    }

    public DebtLoan loanUserId(Long loanUserId) {
        this.setLoanUserId(loanUserId);
        return this;
    }

    public void setLoanUserId(Long loanUserId) {
        this.loanUserId = loanUserId;
    }

    public Long getDebtUserId() {
        return this.debtUserId;
    }

    public DebtLoan debtUserId(Long debtUserId) {
        this.setDebtUserId(debtUserId);
        return this;
    }

    public void setDebtUserId(Long debtUserId) {
        this.debtUserId = debtUserId;
    }

    public Double getCost() {
        return this.cost;
    }

    public DebtLoan cost(Double cost) {
        this.setCost(cost);
        return this;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Instant getDeadline() {
        return this.deadline;
    }

    public DebtLoan deadline(Instant deadline) {
        this.setDeadline(deadline);
        return this;
    }

    public void setDeadline(Instant deadline) {
        this.deadline = deadline;
    }

    public Instant getDatOfPayment() {
        return this.datOfPayment;
    }

    public DebtLoan datOfPayment(Instant datOfPayment) {
        this.setDatOfPayment(datOfPayment);
        return this;
    }

    public void setDatOfPayment(Instant datOfPayment) {
        this.datOfPayment = datOfPayment;
    }

    public Profiles getDebts() {
        return this.debts;
    }

    public void setDebts(Profiles profiles) {
        this.debts = profiles;
        this.debtsId = profiles != null ? profiles.getProfileId() : null;
    }

    public DebtLoan debts(Profiles profiles) {
        this.setDebts(profiles);
        return this;
    }

    public Profiles getLoans() {
        return this.loans;
    }

    public void setLoans(Profiles profiles) {
        this.loans = profiles;
        this.loansId = profiles != null ? profiles.getProfileId() : null;
    }

    public DebtLoan loans(Profiles profiles) {
        this.setLoans(profiles);
        return this;
    }

    public Long getDebtsId() {
        return this.debtsId;
    }

    public void setDebtsId(Long profiles) {
        this.debtsId = profiles;
    }

    public Long getLoansId() {
        return this.loansId;
    }

    public void setLoansId(Long profiles) {
        this.loansId = profiles;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DebtLoan)) {
            return false;
        }
        return id != null && id.equals(((DebtLoan) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DebtLoan{" +
            "id=" + getId() +
            ", loanUserId=" + getLoanUserId() +
            ", debtUserId=" + getDebtUserId() +
            ", cost=" + getCost() +
            ", deadline='" + getDeadline() + "'" +
            ", datOfPayment='" + getDatOfPayment() + "'" +
            "}";
    }
}
