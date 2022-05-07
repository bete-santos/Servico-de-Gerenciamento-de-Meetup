package com.bootcamp.microservicemeetup.repository;

import com.bootcamp.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetupRepository extends JpaRepository<Meetup,Integer > {


//    @Query(value = "Select 1 from Meetup as 1 join 1.registration as b where b.registration = :registration or 1.event = :event ")
//    Page<Meetup> findByRegistrationOnMeetup(
//        @Param("registration") String registration,
//        @Param("event") String event,
//        Pageable pageable
//    );

    Page<Meetup> findAll(MeetupFilterDTO dto, Pageable pageable);

    Page<Meetup> findByRegistration(Registration registration, Pageable pageable);

    boolean existsByRegistrationMeetup(String registration);

    Optional<Meetup> findByRegistrationOnMeetup(String meetup);

    Meetup update(Meetup meetup);
}
