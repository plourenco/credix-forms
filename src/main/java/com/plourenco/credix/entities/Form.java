package com.plourenco.credix.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name="forms")
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Form {
    @Id
    @GeneratedValue
    @JsonInclude
    private int id;

    @NotBlank
    private String title;

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="form_id")
    private Set<FormQuestion> questions;

    public Form() {

    }

    public Form(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Set<FormQuestion> getQuestions() {
        return questions;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setQuestions(Set<FormQuestion> questions) {
        for (var question : questions) {
            question.setForm(this);
        }
        this.questions = questions;
    }

    public boolean isValid(Map<Integer, String> responses) {
        for (FormQuestion question : questions) {
            String value = responses.get(question.getId());
            if (!question.getType().isValid(value)) return false;
        }
        return true;
    }
}
