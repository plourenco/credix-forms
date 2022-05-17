package com.plourenco.credix.controllers;

import com.plourenco.credix.entities.FormQuestion;
import com.plourenco.credix.entities.FormQuestionResponse;
import com.plourenco.credix.repositories.FormRepository;
import com.plourenco.credix.entities.Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class FormController {

    private final FormRepository repository;

    private final EntityManager entityManager;

    @Autowired
    public FormController(EntityManager entityManager, FormRepository repository) {
        this.entityManager = entityManager;
        this.repository = repository;
    }

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @PostMapping("/forms/definitions")
    public void formCreate(@RequestBody @Valid Form form) {
        for (FormQuestion question : form.getQuestions()) {
            question.setForm(form);
        }
        repository.saveAndFlush(form);
    }

    @GetMapping("/forms")
    public List<Form> forms() {
        return repository.findAll();
    }

    @GetMapping("/forms/{id}")
    public Form formDetails(@PathVariable int id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/forms/{formId}/entries/{questionId}")
    public List<FormQuestionResponse> formEntries(@PathVariable int formId, @PathVariable int questionId) {
        // Assuming a fixed query of entries with more than 3 years
        // Even though we could create dynamic predicates based on query params, or having multiple
        // @Filter(name = 'intFilter', condition = ...) based on the type
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<FormQuestionResponse> query = builder.createQuery(FormQuestionResponse.class);
        Root<FormQuestionResponse> response = query.from(FormQuestionResponse.class);
        Predicate withForm = builder.equal(response.get("question").get("form"), formId);
        Predicate withQuestion = builder.equal(response.get("question"), questionId);
        Predicate hasYears = builder.lessThan(response.get("value").as(Integer.class), 3);
        return entityManager.createQuery(query.where(withForm, withQuestion, hasYears)).getResultList();
    }

    @PostMapping("/forms/{id}/entries")
    public void formSubmit(@PathVariable int id, @RequestBody Map<Integer, String> responses) {
        Form form = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!form.getQuestions().stream().map(FormQuestion::getId).collect(Collectors.toSet())
                .equals(responses.keySet())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!form.isValid(responses)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        for (var question : form.getQuestions()) {
            var response = new FormQuestionResponse(responses.get(question.getId()));
            question.addResponse(response);
        }
        repository.save(form);
    }
}
