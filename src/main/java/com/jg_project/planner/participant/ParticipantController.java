package com.jg_project.planner.participant;

import com.jg_project.planner.participant.dto.ParticipantDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    @Autowired
    private ParticipantRepository participantRepository;

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Participant> confirmParticipant(@PathVariable UUID id, @RequestBody ParticipantDto payload) {

        Optional<Participant> participant = this.participantRepository.findById(id);

        if(participant.isPresent()) {

            Participant newParticipant = participant.get();
            newParticipant.setIsConfirmed(true);
            newParticipant.setName(payload.name());

            participantRepository.save(newParticipant);

            return ResponseEntity.ok(newParticipant);
        }

        return ResponseEntity.notFound().build();

    }
}
