package com.bootcamp.microservicemeetup.repository;

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
public class RegistrationRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    RegistrationRepository repository;

    @Test
    @DisplayName("Should return true when exists an registration already cerated")
    public void returnTrueWhenRegistrationExists() {
        String registration = "123";
        Registration registrationAtributte = createNewRegistration(registration);
        entityManager.persist(registrationAtributte);

        boolean exists = repository.existsByRegistration(registration);

        assertThat(exists).isTrue();
   }

   @Test
   @DisplayName("Should return false when doesn't an registration_atrb with a registration already created")
   public void returnFalseWhenRegistrationAttributteDoesnExists() {

        String registration = "123";

        boolean exists = repository.existsByRegistration(registration);

        assertThat(exists).isFalse();
   }

   @Test
   @DisplayName("Should get an registration by id")
   public void findByIdTest() {

        Registration registration_atributte = createNewRegistration("323");
        entityManager.persist(registration_atributte);

        Optional<Registration> foundRegistration = repository
                .findById(registration_atributte.getId());

        assertThat(foundRegistration.isPresent()).isTrue();
   }

   @Test
   @DisplayName("Should save an registration")
   public void saveRegistrationTest() {

       Registration registration_atributte = createNewRegistration("323");
       Registration savedRegistration = repository.save(registration_atributte);

       assertThat(savedRegistration.getId()).isNotNull();

   }

   @Test
   @DisplayName("Should delete an registration from the base")
   public void deleteRegistration() {

       Registration registration_atributte = createNewRegistration("323");
       entityManager.persist(registration_atributte);

       Registration foundRegistration = entityManager
               .find(Registration.class, registration_atributte.getId());

       repository.delete(foundRegistration);

       Registration deleteRegistration = entityManager
               .find(Registration.class, registration_atributte.getId());
       assertThat(deleteRegistration).isNull();
   }

    private Registration createNewRegistration(String registration) {
        return Registration.builder()
                .name("Bete Santos")
                .dateOfRegistration(LocalDateTime.now())
                .registration(registration)
                .build();
    }
}
