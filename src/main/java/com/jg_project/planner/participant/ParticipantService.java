package com.jg_project.planner.participant;

import com.jg_project.planner.participant.dto.GetAllParticipantsDto;
import com.jg_project.planner.participant.dto.ParticipantResponse;
import com.jg_project.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.stream;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    public void registerParticipantsTrip(List<String> participantsToInvite, Trip trip) {
        List<Participant> participant = participantsToInvite.stream().map(email -> new Participant(email, trip)).toList();

        participantRepository.saveAll(participant);
    }


    public ParticipantResponse registerParticipantToTrip(String email, Trip trip) {
        Participant participant = new Participant(email, trip);
        participantRepository.save(participant);

        return new ParticipantResponse(participant.getId());
    }

    public void triggerConfirmationEmailToParticipants(UUID tripId) {}

    public void triggerConfirmationEmailToParticipant(String email) {}

    public List<GetAllParticipantsDto> getParticipantsByTripId(UUID tripId) {
        return this.participantRepository.findByTripId(tripId).stream().map(participant -> new GetAllParticipantsDto(participant.getId(), participant.getName(), participant.getEmail(), participant.getIsConfirmed())).toList();
    }
}
