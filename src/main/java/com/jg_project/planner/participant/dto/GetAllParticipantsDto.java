package com.jg_project.planner.participant.dto;

import java.util.UUID;

public record GetAllParticipantsDto(UUID id, String name, String email, Boolean is_confirmed) {
}
