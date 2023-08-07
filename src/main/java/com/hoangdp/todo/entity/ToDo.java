package com.hoangdp.todo.entity;

import java.time.Instant;

import com.hoangdp.todo.enums.StatusToDoEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "todos")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToDo extends HistoryEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "deadline")
    private Instant deadline;

    @Column(name = "status", nullable = false)
    @Builder.Default
    @Enumerated
    private StatusToDoEnum status = StatusToDoEnum.PLANNED;

}
