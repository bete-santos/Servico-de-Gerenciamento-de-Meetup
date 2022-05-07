package com.bootcamp.microservicemeetup.controller.resource;

import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.bootcamp.microservicemeetup.controller.dto.RegistrationDTO;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.service.RegistrationService;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.service.MeetupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meetups")
@RequiredArgsConstructor
public class MeetupController {

    @Autowired
    private final MeetupService meetupService;

    @Autowired
    private final RegistrationService registrationService;

    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private Integer create(@RequestBody MeetupDTO meetupDTO) {

        Registration registration = registrationService
                .getRegistrationById(meetupDTO.getRegistration().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        Meetup entity = Meetup.builder()
                .event(meetupDTO.getEvent())
                .registration(registration)
                .meetupDate(LocalDateTime.now())
                .build();

        entity = meetupService.save(entity);
        return entity.getId();
    }

    @GetMapping
    public Page<MeetupDTO> findAll(MeetupFilterDTO dto, Pageable pageRequest) {
        Page<Meetup> result = meetupService.findAll(dto, pageRequest);
        List<MeetupDTO> meetups = result
                .getContent()
                .stream()
                .map(entity -> {

                    Registration registration = entity.getRegistration();
                    RegistrationDTO registrationDTO = modelMapper.map(registration, RegistrationDTO.class);

                    MeetupDTO meetupDTO = modelMapper.map(entity, MeetupDTO.class);
                    //meetupDTO.setRegistration(registrationDTO);
                    return meetupDTO;

                }).collect(Collectors.toList());
        return new PageImpl<MeetupDTO>(meetups, pageRequest, result.getTotalElements());
    }

}

