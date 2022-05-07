package com.bootcamp.microservicemeetup.repository;

import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class MeetupRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    MeetupRepository repository;

    @Test
    @DisplayName("Should return true when exists an registration the meetup already created")
    public void returnTrueWhenRegistrationMeetsExists() {
        String registration = "02";
        Meetup meetupAtributte = createNewMeetupRegistration(registration);
        entityManager.persist(meetupAtributte);

        boolean exists = repository.existsByRegistrationMeetup(registration);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when doesn't an registration_atrb  with a meetup already created")
    public void returnFalseWhenRegistrationMeetupAttributteDoesnExists() {

        String registration = "02";

        boolean exists = repository.existsByRegistrationMeetup(registration);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should get an registration meetup by id")
    public void findByIdTest() {

        Meetup registration_atributte_meetup = createNewMeetupRegistration("02");
        entityManager.persist(registration_atributte_meetup);

        Optional<Meetup> foundRegistration = repository
                .findById(registration_atributte_meetup.getId());

        assertThat(foundRegistration.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should save an registration meetup")
    public void saveRegistrationMeetupTest() {

        Meetup registration_atributte_meetup = createNewMeetupRegistration("02");
        Meetup savedRegistrationMeetup = repository.save(registration_atributte_meetup);

        assertThat(savedRegistrationMeetup.getId()).isNotNull();

    }

    @Test
    @DisplayName("Should delete an registration the meetup from the base")
    public void deleteRegistrationMeetup() {

        Meetup registration_atributte_meetup = createNewMeetupRegistration("02");
        entityManager.persist(registration_atributte_meetup);

        Meetup foundRegistrationMeetup = entityManager
                .find(Meetup.class, registration_atributte_meetup.getId());

        repository.delete(foundRegistrationMeetup);

        Meetup deleteRegistrationMeetup = entityManager
                .find(Meetup.class, registration_atributte_meetup.getId());
        assertThat(deleteRegistrationMeetup).isNull();
    }

    private Meetup createNewMeetupRegistration(String meetup) {
        return Meetup.builder()
                .meetupDate(LocalDateTime.now())
                .event("Assis")
                .registered(true)
                .registration(Registration.builder()
                        .registration("02")
                        .build())
                .build();
    }
}
