package com.jg_project.planner.participant;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {
    public void registerParticipantsTrip(List<String> participantsToInvite, UUID id) {}

    public void triggerConfirmationEmailToParticipants(UUID tripId) {}
}
