package com.bootcamp.microservicemeetup.service.impl;

import com.bootcamp.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.repository.MeetupRepository;
import com.bootcamp.microservicemeetup.service.MeetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MeetupServiceImpl implements MeetupService {

    @Autowired
    private MeetupRepository repository;

    public MeetupServiceImpl(MeetupRepository repository) {
        this.repository = repository;
    }

    @Override
    public Meetup save(Meetup meetup) {
        return repository.save(meetup);
    }

    @Override
    public Page<Meetup> findAll(MeetupFilterDTO dto, Pageable pageable) {
        return repository.findAll(dto, pageable);
    }

    @Override
    public Optional<Meetup> getRegistrationByMeetupId(Integer id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Meetup meetup) {
        if (meetup == null || meetup.getId() == null) {
            throw  new IllegalArgumentException("Registration Meetup id cannot be null");
        }
        this.repository.delete(meetup);
    }

    @Override
    public Meetup update(Meetup meetup) {
        return repository.update(meetup);
    }

    @Override
    public Optional<Meetup> getRegistrationByRegistration(String meetup) {
        return repository.findByRegistrationOnMeetup(meetup);
    }
}
