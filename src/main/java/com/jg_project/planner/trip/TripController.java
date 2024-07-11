package com.jg_project.planner.trip;

import com.jg_project.planner.participant.Participant;
import com.jg_project.planner.participant.ParticipantService;
import com.jg_project.planner.participant.dto.GetAllParticipantsDto;
import com.jg_project.planner.participant.dto.ParticipantDto;
import com.jg_project.planner.participant.dto.ParticipantResponse;
import com.jg_project.planner.trip.dto.TripDto;
import com.jg_project.planner.trip.dto.TripResponseDto;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

        this.participantService.registerParticipantsTrip(payload.emails_to_invite(), newTrip);

        return ResponseEntity.ok().body(new TripResponseDto(newTrip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id) {

        Optional<Trip> trip = this.tripRepository.findById(id);

        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripDto payload) {

        Optional<Trip> trip = this.tripRepository.findById(id);

        if(trip.isPresent()) {
            Trip tripToUpdate = trip.get();
            tripToUpdate.setEndsAt(LocalDateTime.parse(payload.ends_at()));
            tripToUpdate.setStartAt(LocalDateTime.parse(payload.start_at()));
            tripToUpdate.setDestination(payload.destination());
            this.tripRepository.save(tripToUpdate);

            return ResponseEntity.ok(tripToUpdate);
        }

        return ResponseEntity.notFound().build();

    }

    @GetMapping("/confirm/{id}")
    public ResponseEntity<Trip> tripConfirm(@PathVariable UUID id) {

        Optional<Trip> trip = this.tripRepository.findById(id);

        if(trip.isPresent()) {
            Trip tripToUpdate = trip.get();
            tripToUpdate.setIsConfirmed(true);
            this.tripRepository.save(tripToUpdate);
            this.participantService.triggerConfirmationEmailToParticipants(id);

            return ResponseEntity.ok(tripToUpdate);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<ParticipantResponse> createTrip(@PathVariable UUID id, @RequestBody ParticipantDto payload) {

        Optional<Trip> trip = this.tripRepository.findById(id);

        if(trip.isPresent()) {
            Trip tripToUpdate = trip.get();
            ParticipantResponse participantRes = this.participantService.registerParticipantToTrip(payload.email(), tripToUpdate);

            if(tripToUpdate.getIsConfirmed()) this.participantService.triggerConfirmationEmailToParticipant(payload.email());

            return ResponseEntity.ok(participantRes);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantDto payload) {

        Optional<Trip> trip = this.tripRepository.findById(id);

        if(trip.isPresent()) {
            Trip tripToUpdate = trip.get();
            ParticipantResponse participantRes = this.participantService.registerParticipantToTrip(payload.email(), tripToUpdate);

            if(tripToUpdate.getIsConfirmed()) this.participantService.triggerConfirmationEmailToParticipant(payload.email());

            return ResponseEntity.ok(participantRes);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<GetAllParticipantsDto>> getParticipants(@PathVariable UUID id) {
        List<GetAllParticipantsDto> participants = this.participantService.getParticipantsByTripId(id);
        return ResponseEntity.ok(participants);
    }
}
