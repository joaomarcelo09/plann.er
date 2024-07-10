package com.jg_project.planner.trip;

import com.jg_project.planner.participant.ParticipantService;
import com.jg_project.planner.trip.dto.TripDto;
import com.jg_project.planner.trip.dto.TripResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private TripRepository tripRepository;

    @PostMapping
    public ResponseEntity<TripResponseDto> createTrip(@RequestBody TripDto payload) {

        Trip newTrip = new Trip(payload);

        this.tripRepository.save(newTrip);

        this.participantService.registerParticipantsTrip(payload.emails_to_invite(), newTrip.getId());

        return ResponseEntity.ok().body(new TripResponseDto(newTrip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id) {

        Optional<Trip> trip = this.tripRepository.findById(id);

        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }
}
