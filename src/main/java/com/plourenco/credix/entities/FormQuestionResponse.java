package com.plourenco.credix.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name="form_question_responses")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FormQuestionResponse {
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable=false)
    @NotBlank
    private String value;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="question_id", nullable=false)
    private FormQuestion question;

    public FormQuestionResponse() {

    }

    public FormQuestionResponse(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public FormQuestion getQuestion() {
        return question;
    }

    public void setQuestion(FormQuestion question) {
        this.question = question;
    }
}
