package com.dmiit3iy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "card",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"question", "answer","category_id"})})
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NonNull
    private String question;

    @Column(nullable = false)
    @NonNull
    private String answer;

    @ManyToOne
    @NonNull
    @JsonIgnore
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @JsonIgnore
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @Column(nullable = false)
    private LocalDateTime creationDate;
}
