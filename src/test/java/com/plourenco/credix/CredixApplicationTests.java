package com.plourenco.credix;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plourenco.credix.entities.Form;
import com.plourenco.credix.entities.FormQuestion;
import com.plourenco.credix.repositories.FormRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CredixApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testEmptyForm() throws Exception {
        mvc.perform(post("/forms/definitions")).andExpect(status().is4xxClientError());
    }

    private static Stream<Arguments> getBorrowerResponses() {
        return Stream.of(
                Arguments.of(Arrays.asList("a 55", "[2838, 2837]", "6"), status().is2xxSuccessful()),
                Arguments.of(Arrays.asList("Tecredi", "[4726, 348]", "2"), status().is2xxSuccessful()),
                Arguments.of(Arrays.asList("Provi", "[482, 2833]", "8"), status().is2xxSuccessful()),
                Arguments.of(Arrays.asList("Provi", "[]", "foo"), status().is4xxClientError())
        );
    }

    @ParameterizedTest
    @MethodSource("getBorrowerResponses")
    void testBorrowerForm(List<String> responses, ResultMatcher result) throws Exception {
        var questions = Map.of(
                new FormQuestion("What is the name of your company?", QuestionType.STRING), responses.get(0),
                new FormQuestion("Where is your company located?", QuestionType.TUPLE), responses.get(1),
                new FormQuestion("How long does your company exists for?", QuestionType.INTEGER), responses.get(2)
        );
        Form form = new Form("My title");
        form.setQuestions(questions.keySet());
        formRepository.save(form);
        var reqBody = questions.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));
        mvc.perform(post(String.format("/forms/%d/entries", form.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(result);
    }

    private static Stream<Arguments> getInvestorResponses() {
        return Stream.of(
                Arguments.of(Arrays.asList("5000000", "True", "2022-01-21", "73"), status().is2xxSuccessful()),
                Arguments.of(Arrays.asList("10M", "1", "2006-03-09", "23"), status().is4xxClientError())
        );
    }

    @ParameterizedTest
    @MethodSource("getInvestorResponses")
    void test(List<String> responses, ResultMatcher result) throws Exception {
        var questions = Map.of(
                new FormQuestion("How much capital do you want to invest?", QuestionType.INTEGER), responses.get(0),
                new FormQuestion("Are you interested in investing in individual deals?", QuestionType.BOOLEAN), responses.get(1),
                new FormQuestion("What was the date your company was founded?", QuestionType.DATE), responses.get(2),
                new FormQuestion("How many people work at your firm?", QuestionType.INTEGER), responses.get(3)
        );
        Form form = new Form("My title");
        form.setQuestions(questions.keySet());
        formRepository.save(form);
        var reqBody = questions.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));
        mvc.perform(post(String.format("/forms/%d/entries", form.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(result);
    }
}
