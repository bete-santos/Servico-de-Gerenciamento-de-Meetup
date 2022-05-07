package com.bootcamp.microservicemeetup.service;

import com.bootcamp.microservicemeetup.exception.BussinessException;
import com.bootcamp.microservicemeetup.repository.RegistrationRepository;
import com.bootcamp.microservicemeetup.model.entity.Registration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.bootcamp.microservicemeetup.service.impl.RegistrationServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class )
@ActiveProfiles("test")
public class RegistrationServiceTest {

    RegistrationService service;

    @MockBean
    RegistrationRepository repository;

    @BeforeEach
    public void SetUp() {
        this.service = new RegistrationServiceImpl(repository);

    }

    @Test
    @DisplayName("Should save an registration")
    public void saveStudent() {
        //cenario
        Registration registration = createValidRegistration();

        //execucao
        Mockito.when(repository.existsByRegistration(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(registration)).thenReturn(createValidRegistration());

        Registration savedRegistration = service.save(registration);

        //Assert
        assertThat(savedRegistration.getId()).isEqualTo(10);
        assertThat(savedRegistration.getName()).isEqualTo("Bete Santos");
        assertThat(savedRegistration.getDateOfRegistration()).isEqualTo(LocalDateTime.now());
        assertThat(savedRegistration.getRegistration()).isEqualTo("001");

    }

    @Test
    @DisplayName("Should throw bussines exception when thy" +
            "to save a new registration with a registration duplicated")
    public void shouldNotSaveAsRegistrationDuplicated() {

        Registration registration = createValidRegistration();
        Mockito.when(repository.existsByRegistration(Mockito.any())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(registration));
        assertThat(exception)
                .isInstanceOf(BussinessException.class)
                .hasMessage("registration already created");

        Mockito.verify(repository, Mockito.never()).save(registration);
    }
    @Test
    @DisplayName("Should get an Registration by id")
    public void getByRegistrationIdTest() {

        //cenario
        Integer id = 10;
        Registration registration = createValidRegistration();
        registration.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(registration));

        //execucao
        Optional<Registration> foundRegistration = service.getRegistrationById(id);

        assertThat(foundRegistration.isPresent()).isTrue();
        assertThat(foundRegistration.get().getId()).isEqualTo(id);
        assertThat(foundRegistration.get().getName()).isEqualTo(registration.getName());
        assertThat(foundRegistration.get().getDateOfRegistration()).isEqualTo(registration.getDateOfRegistration());
        assertThat(foundRegistration.get().getRegistration()).isEqualTo(registration.getRegistration());

    }

    @Test
    @DisplayName("Should return empty when get an registration by id when doesn't exists")
    public void registrationNotFoundIdTest() {

        Integer id = 10;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<Registration> registration = service.getRegistrationById(id);

        assertThat(registration.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should delete an student")
    public void deleteRegistrationTest() {
        Registration registration = Registration.builder().id(11).build();
        //não vai lançar exceção para deletar
        assertDoesNotThrow(()-> service.delete(registration));

        Mockito.verify(repository, Mockito.times(1)).delete(registration);
    }

    @Test
    @DisplayName("Should update an registration")
    public void updateRegistration() {

        //cenario
         Integer id = 10;
         Registration updatingRegistration = Registration.builder()
                 .id(10)
                 .build();

         //execução
        Registration updateRegistration = createValidRegistration();
        updateRegistration.setId(id);

        Mockito.when(repository.save(updatingRegistration)).thenReturn(updateRegistration);
        Registration registration = service.update(updatingRegistration);

        //Assert
        assertThat(registration.getId()).isEqualTo(updateRegistration.getId());
        assertThat(registration.getName()).isEqualTo(updateRegistration.getName());
        assertThat(registration.getDateOfRegistration()).isEqualTo(updateRegistration.getDateOfRegistration());
        assertThat(registration.getRegistration()).isEqualTo(updateRegistration.getRegistration());
    }

    @Test
    @DisplayName("Should filter registration must by properties")
    public void findRegistrationTest() {

        //cenario
        Registration registration = createValidRegistration();
        PageRequest pageRequest = PageRequest.of(0, 20);

        List<Registration> listRegistrations = Arrays.asList(registration);
        Page<Registration> page = new PageImpl<Registration>(Arrays.asList(registration),
                PageRequest.of(0, 10),1);

        //execução
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);

        Page<Registration> result = service.find(registration, pageRequest);

        //assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(listRegistrations);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should get an Registration model by registration attribute")
    public void getRegistrationByRegistration() {
        String registrationAttribute = "1234";

        Mockito.when(repository.findByRegistration(registrationAttribute))
                .thenReturn(Optional.of(Registration.builder().id(10).registration(registrationAttribute).build()));

        Optional<Registration> registration = service.getRegistrationByRegistrationAtribute(registrationAttribute);

        assertThat(registration.isPresent()).isTrue();
        assertThat(registration.get().getId()).isEqualTo(10);
        assertThat(registration.get().getRegistration()).isEqualTo(registrationAttribute);

        Mockito.verify(repository, Mockito.times(1)).findByRegistration(registrationAttribute);

    }


    private Registration createValidRegistration() {
        return Registration.builder()
                .id(10)
                .name("Bete Santos")
                .dateOfRegistration(LocalDateTime.now())
                .registration("001")
                .communityMeetup("WoMarkers")
                .build();

    }

}

