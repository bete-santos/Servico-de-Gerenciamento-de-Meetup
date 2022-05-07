package com.bootcamp.microservicemeetup.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Registration {

    @Id
    @Column(name = "registration_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String communityMeetup;

    @Column(name = "person_name")
    private String name;

    @Column(name = "date_of_registration")
    private LocalDateTime dateOfRegistration;

    @Column
    private String registration;
}
