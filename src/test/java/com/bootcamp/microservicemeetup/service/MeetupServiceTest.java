package com.bootcamp.microservicemeetup.service;

import com.bootcamp.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.bootcamp.microservicemeetup.exception.BussinessException;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.repository.MeetupRepository;
import com.bootcamp.microservicemeetup.service.impl.MeetupServiceImpl;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(SpringExtension.class )
@ActiveProfiles("test")
public class MeetupServiceTest {


    MeetupService service;

    @MockBean
    MeetupRepository repository;

    @BeforeEach
    public void SetUp() {
        this.service = new MeetupServiceImpl(repository);

    }

    @Test
    @DisplayName("Should save an registration")
    public void saveRegistrationMeetup() {
        //cenario
        Meetup meetup = createValidRegistrationMeetup();

        //execucao
        Mockito.when(repository.existsByRegistrationMeetup(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(meetup)).thenReturn(createValidRegistrationMeetup());

        Meetup savedRegistrationMeetup = service.save(meetup);

        //Assert
        assertThat(savedRegistrationMeetup.getId()).isEqualTo(12);
        assertThat(savedRegistrationMeetup.getRegistered()).isTrue();
        assertThat(savedRegistrationMeetup.getRegistration()).isEqualTo("02");
        assertThat(savedRegistrationMeetup.getMeetupDate()).isEqualTo(LocalDateTime.now());
        assertThat(savedRegistrationMeetup.getEvent()).isEqualTo("Meetup WoMarkers");

    }

    @Test
    @DisplayName("Should throw bussines exception when thy" +
            "to save a new registration meetup with a registration duplicated")
    public void shouldNotSaveAsRegistrationDuplicated() {

        Meetup meetup = createValidRegistrationMeetup();
        Mockito.when(repository.existsByRegistrationMeetup(Mockito.any())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(meetup));
        assertThat(exception)
                .isInstanceOf(BussinessException.class)
                .hasMessage("registration meetup already created");

        Mockito.verify(repository, Mockito.never()).save(meetup);
    }
    @Test
    @DisplayName("Should get an Registration Meetup by id")
    public void getByRegistrationMeetupIdTest() {

        //cenario
        Integer id = 10;
        Meetup meetup = createValidRegistrationMeetup();
        meetup.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(meetup));

        //execucao
        Optional<Meetup> foundRegistrationMeetup = service.getRegistrationByMeetupId(id);

        assertThat(foundRegistrationMeetup.isPresent()).isTrue();
        assertThat(foundRegistrationMeetup.get().getId()).isEqualTo(id);
        assertThat(foundRegistrationMeetup.get().getRegistered()).isTrue();
        assertThat(foundRegistrationMeetup.get().getRegistration()).isEqualTo("02");
        assertThat(foundRegistrationMeetup.get().getMeetupDate()).isEqualTo(LocalDateTime.now());
        assertThat(foundRegistrationMeetup.get().getEvent()).isEqualTo("Meetup WoMarkers");
    }

    @Test
    @DisplayName("Should return empty when get an registration meetup by id when doesn't exists")
    public void registrationMeetupNotFoundIdTest() {

        Integer id = 10;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<Meetup> meetup= service.getRegistrationByMeetupId(id);

        assertThat(meetup.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should delete an registration meetup")
    public void deleteRegistrationMeetupTest() {
        Meetup meetup = Meetup.builder().id(11).build();

        //não vai lançar exceção para deletar
        assertDoesNotThrow(()-> service.delete(meetup));

        Mockito.verify(repository, Mockito.times(1)).delete(meetup);
    }

    @Test
    @DisplayName("Should update an registration meetup")
    public void updateRegistrationMeetup() {

        //cenario
        Integer id = 10;
        Meetup updatingRegistrationMeetup = Meetup.builder()
                .id(10)
                .build();

        //execução
        Meetup updateRegistrationMeetup = createValidRegistrationMeetup();
        updateRegistrationMeetup.setId(id);

        Mockito.when(repository.save(updatingRegistrationMeetup)).thenReturn(updateRegistrationMeetup);
        Meetup registrationMeetup = service.update(updatingRegistrationMeetup);

        //Assert
        assertThat(registrationMeetup.getId()).isEqualTo(updateRegistrationMeetup.getId());
        assertThat(registrationMeetup.getRegistered()).isTrue();
        assertThat(registrationMeetup.getRegistration()).isEqualTo(updateRegistrationMeetup.getRegistration());
        assertThat(registrationMeetup.getMeetupDate()).isEqualTo(updateRegistrationMeetup.getMeetupDate());
        assertThat(registrationMeetup.getEvent()).isEqualTo(updateRegistrationMeetup.getEvent());

    }

    @Test
    @DisplayName("Should filter registration meetup must by properties")
    public void findRegistrationMeetupTest() {

        //cenario
        MeetupFilterDTO registrationMeetup = createValidRegistrationMeetupFilter();
        PageRequest pageRequest = PageRequest.of(0, 20);

        List<MeetupFilterDTO> listRegistrationMeetups = Arrays.asList(registrationMeetup);
        Page<MeetupFilterDTO> page = new PageImpl<MeetupFilterDTO>(Arrays.asList(registrationMeetup),
                PageRequest.of(0, 10),1);

        //execução
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);

        Page<Meetup> result = service.findAll(registrationMeetup, pageRequest);

        //assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(listRegistrationMeetups);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

//    @Test
//    @DisplayName("Should get an Registration model by registration attribute")
//    public void getRegistrationByRegistrationMeetup() {
//
//       String registrationMeetupAttribute = "1234";
//
//        Mockito.when(repository.findByRegistrationOnMeetup())
//               .thenReturn(Optional.of(Meetup.builder().id(10).registration(Registration.builder().build()).build()));
//
//        Optional<Meetup> registrationMeetups = service.getRegistrationByRegistration(registrationMeetupAttribute);
//
//        assertThat(registrationMeetups.isPresent()).isTrue();
//        assertThat(registrationMeetups.get().getId()).isEqualTo(10);
//        assertThat(registrationMeetups.get().getRegistration()).isEqualTo(registrationMeetupAttribute);
//
//        Mockito.verify(repository, Mockito.times(1)).findByRegistration(re);
//
//    }


    private Meetup createValidRegistrationMeetup() {
        return Meetup.builder()
                .id(10)
                .registered(true)
                .event("Meetup WoMarkers")
                .meetupDate(LocalDateTime.now())
                .registration(Registration.builder()
                        .registration("02")
                        .build())
                .build();
    }

    private MeetupFilterDTO createValidRegistrationMeetupFilter() {
        return MeetupFilterDTO.builder()
              .registration("02")
              .event("Meetup WoMarkers")
              .build();
    }
}
