package com.bootcamp.microservicemeetup.controller.resource;

import com.bootcamp.microservicemeetup.controller.dto.RegistrationDTO;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationController {

    @Autowired
    RegistrationService registrationService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Integer create(@RequestBody @Valid RegistrationDTO registrationDTO) {

        Registration entity = modelMapper.map(registrationDTO, Registration.class);
        entity = registrationService.save(entity);

        return entity.getId();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public RegistrationDTO get (@PathVariable Integer id) {

        return registrationService
                .getRegistrationById(id)
                .map(registration -> modelMapper.map(registration, RegistrationDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping({"id"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByRegistration(@PathVariable Integer id) {
        Registration registration = registrationService
                .getRegistrationById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                 registrationService.delete(registration);
    }

    @PutMapping("{id}")
    public RegistrationDTO update(@PathVariable Integer id, RegistrationDTO registrationDTO) {

        return registrationService.getRegistrationById(id).map(registration -> {
            registration.setName(registrationDTO.getName());
            registration.setDateOfRegistration(registrationDTO.getDateOfRegistration());
            registration.setRegistration(registration.getRegistration());
            registration = registrationService.update(registration);

            return modelMapper.map(registration, RegistrationDTO.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @GetMapping
    public Page<RegistrationDTO> find(RegistrationDTO dto, Pageable pageRequest) {
        Registration filter = modelMapper.map(dto, Registration.class);
        Page<Registration> result = registrationService.find(filter, pageRequest);

        List<RegistrationDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, RegistrationDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<RegistrationDTO>(list, pageRequest, result.getTotalElements());
    }
}
