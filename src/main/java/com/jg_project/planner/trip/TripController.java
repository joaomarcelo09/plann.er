package com.jg_project.planner.trip;

import com.jg_project.planner.activity.dto.ActivityData;
import com.jg_project.planner.activity.dto.ActivityDto;
import com.jg_project.planner.activity.dto.ActivityResponse;
import com.jg_project.planner.activity.ActivityService;
import com.jg_project.planner.link.LinkService;
import com.jg_project.planner.link.dto.LinkData;
import com.jg_project.planner.link.dto.LinkDto;
import com.jg_project.planner.link.dto.LinkResponse;
import com.jg_project.planner.participant.ParticipantService;
import com.jg_project.planner.participant.dto.GetAllParticipantsDto;
import com.jg_project.planner.participant.dto.ParticipantDto;
import com.jg_project.planner.participant.dto.ParticipantResponse;
import com.jg_project.planner.trip.dto.TripDto;
import com.jg_project.planner.trip.dto.TripResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LinkService linkService;

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

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getActivities(@PathVariable UUID id) {
        List<ActivityData> activities = this.activityService.getAllActivities(id);
        return ResponseEntity.ok(activities);
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> createActivity(@PathVariable UUID id, @RequestBody ActivityDto payload) {

        Optional<Trip> trip = this.tripRepository.findById(id);

        if(trip.isPresent()) {
            Trip tripToUpdate = trip.get();

            ActivityResponse activityResponse = this.activityService.saveActivity(payload, tripToUpdate);

            return ResponseEntity.ok(activityResponse);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> createLink(@PathVariable UUID id, @RequestBody LinkDto payload) {

        Optional<Trip> trip = this.tripRepository.findById(id);

        if(trip.isPresent()) {
            Trip tripToUpdate = trip.get();

            LinkResponse linkResponse = this.linkService.saveLinks(payload, tripToUpdate);

            return ResponseEntity.ok(linkResponse);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getLinks(@PathVariable UUID id) {
        List<LinkData> links = this.linkService.getAllLinks(id);
        return ResponseEntity.ok(links);
    }
}
