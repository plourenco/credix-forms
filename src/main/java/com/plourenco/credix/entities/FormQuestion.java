package com.plourenco.credix.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.plourenco.credix.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name="form_questions")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class FormQuestion {
    @Id
    @GeneratedValue
    @Schema(hidden=true)
    private int id;

    @Column(nullable=false)
    @NotBlank
    private String title;

    @Column(nullable=false)
    @NotNull
    private QuestionType type;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="form_id", nullable=false)
    @JsonIgnore
    private Form form;

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="question_id")
    @JsonIgnore
    private Set<FormQuestionResponse> responses;

    public FormQuestion() {

    }

    public FormQuestion(String title, QuestionType type) {
        this.title = title;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public QuestionType getType() {
        return type;
    }

    public Form getForm() {
        return form;
    }

    public Set<FormQuestionResponse> getResponses() {
        return responses;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public void setResponses(Set<FormQuestionResponse> responses) {
        this.responses = responses;
    }

    public void addResponse(FormQuestionResponse response) {
        response.setQuestion(this);
        this.responses.add(response);
    }
}
