package com.bootcamp.microservicemeetup.repository;

import com.bootcamp.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MeetupRepository extends JpaRepository<Meetup,Integer > {


    @Query(value = "Select 1 from Meetup as meetup join meetup.registration as registration" +
            " where registration.id = :registrationId")
    boolean existsByRegistrationName(
        @Param("registrationId") String registration
    );

    Page<Meetup> findByRegistration(Registration registration, Pageable pageable);

    boolean existsByRegistration(Registration registration);// existe um meetup que possui esse registration?

}
