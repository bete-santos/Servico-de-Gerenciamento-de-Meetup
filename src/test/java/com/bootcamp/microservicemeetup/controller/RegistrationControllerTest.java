package com.bootcamp.microservicemeetup.controller;

import com.bootcamp.microservicemeetup.controller.dto.RegistrationDTO;
import com.bootcamp.microservicemeetup.controller.resource.RegistrationController;
import com.bootcamp.microservicemeetup.exception.BussinessException;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.service.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {RegistrationController.class})
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    static String REGISTRATION_API = "/api/registration";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RegistrationService service;


    @Test
    @DisplayName("Should create a registration with sucess")
    public void createRegistrationTest() throws Exception {

        RegistrationDTO registrationDTOBuilder = createNewRegistration();

        Registration savedRegistration = Registration.builder()
                .id(10)
                .name("Bete")
                .dateOfRegistration(LocalDateTime.now())
                .registration("001")
                .build();
//simula camada do usuario
        BDDMockito.given(service.save(any(Registration.class))).willReturn(savedRegistration);

        String json = new ObjectMapper().writeValueAsString(registrationDTOBuilder);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //verificacao
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(10))
                .andExpect(jsonPath("name").value(registrationDTOBuilder.getName()))
                .andExpect(jsonPath("dateOfRegistration").value(registrationDTOBuilder.getDateOfRegistration()))
                .andExpect(jsonPath("registration").value(registrationDTOBuilder.getRegistration()));
    }

    @Test
    @DisplayName("Should throw an excetion when not have data enough for test")
    public void createInvalidRegistrationTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new RegistrationDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get Registration information")
    public void getRegistrationTest() throws Exception {

        Integer id = 11;

        Registration registration = Registration.builder()
                .id(id)
                .name(createNewRegistration().getName())
                .dateOfRegistration(LocalDateTime.now())
                .registration(createNewRegistration().getRegistration())
                .build();

        BDDMockito.given(service.save(any(Registration.class))).willReturn(registration);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(REGISTRATION_API.concat("/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //verificacao
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(10))
                .andExpect(jsonPath("name").value(createNewRegistration().getName()))
                .andExpect(jsonPath("dateOfRegistration").value(createNewRegistration().getDateOfRegistration()))
                .andExpect(jsonPath("registration").value(createNewRegistration().getRegistration()));
    }

    @Test
    @DisplayName("Should throw an exception when try to create a new registration with a another registration created")
    public void createRegistrationWithDuplicated() throws Exception {

        RegistrationDTO registrationDTO = createNewRegistration();

        String json = new ObjectMapper().writeValueAsString(registrationDTO);

        //simula camada do usuario
        BDDMockito.given(service.save(any(Registration.class)))
                .willThrow(new BussinessException("Registration already create"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //verificacao
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Registration already create"));
    }

    @Test
    @DisplayName("Should return not found when the registration doesn't exists")
    public void registrationNotFoundTest() throws Exception {
        BDDMockito.given(service.getRegistrationById(anyInt())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        //verificacao
        mockMvc.perform(requestBuilder).andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Should delete registration")
    public void deleteRegistration() throws Exception {
        BDDMockito.given(service.getRegistrationById(anyInt()))
                .willReturn(Optional.of(Registration.builder()
                .id(11)
                .build()));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(REGISTRATION_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        //verificacao
        mockMvc.perform(requestBuilder).andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Should delete registration")
    public void deleteNonExistentRegistrationTest() throws Exception {
        BDDMockito.given(service.getRegistrationById(anyInt()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(REGISTRATION_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        //verificacao
        mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update when registration info")
    public void updateRegistrationTest() throws Exception {

        Integer id = 11;
        String json = new ObjectMapper().writeValueAsString(createNewRegistration());

        Registration updatingRegistration = Registration.builder()
                .id(id)
                .name("Enzo Fracasso")
                .dateOfRegistration(LocalDateTime.now())
                .registration("003")
                .build();

        BDDMockito.given(service.getRegistrationById(anyInt()))
                .willReturn(Optional.of(updatingRegistration));

        Registration updatedRegistration = Registration.builder()
                .id(id)
                .name("Bete Fracasso")
                .dateOfRegistration(LocalDateTime.now())
                .registration("003")
                .build();

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(REGISTRATION_API.concat("/" + 1))
                .contentType(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        //verificacao
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value(createNewRegistration().getName()))
                .andExpect(jsonPath("dateOfRegistration").value(createNewRegistration().getDateOfRegistration()))
                .andExpect(jsonPath("registration").value(10));
    }

    @Test
    @DisplayName("Should return 404 when try to update an registration no exist")
    public void updateNonExistentRegistrationTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(createNewRegistration());
        BDDMockito.given(service.getRegistrationById(anyInt()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(REGISTRATION_API.concat("/" + 1))
                .contentType(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should filter registration")
    public void findRegistrationTest() throws Exception {

        Integer id = 10;

        Registration registration = Registration.builder()
                .id(id)
                .name(createNewRegistration().getName())
                .dateOfRegistration(LocalDateTime.now())
                .registration(createNewRegistration().getRegistration())
                .build();

        BDDMockito.given(service.find(Mockito.any(Registration.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Registration>(Arrays.asList(registration), PageRequest.of(0, 100), 1));

        String queryString = String.format("?name=%s&dateOfRegistration=%spage=0&size=100",
                registration.getRegistration(), registration.getDateOfRegistration());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        //verificacao
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(10))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private RegistrationDTO createNewRegistration() {
        return RegistrationDTO.builder()
                .id(10)
                .name("Bete Santos")
                .dateOfRegistration(LocalDateTime.now())
                .registration("001")
                .communityMeetup("WoMarkers")
                .build();
    }
}
