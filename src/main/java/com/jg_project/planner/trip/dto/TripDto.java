package com.jg_project.planner.trip.dto;

import java.util.List;

public record TripDto(String destination, String start_at, String ends_at, List<String> emails_to_invite, String owner_email, String owner_name) {
}
