package com.bootcamp.microservicemeetup.service;

import com.bootcamp.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MeetupService {

    Meetup save(Meetup meetup);

    Page<Meetup> findAll(MeetupFilterDTO dto, Pageable pageable);

    Optional<Meetup> getRegistrationByMeetupId(Integer id);

    void delete(Meetup meetup);

    Meetup update(Meetup meetup);

    Optional<Meetup> getRegistrationByRegistration(String meetup);

}
