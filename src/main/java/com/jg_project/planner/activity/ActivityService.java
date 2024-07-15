package com.jg_project.planner.activity;

import com.jg_project.planner.activity.dto.ActivityData;
import com.jg_project.planner.activity.dto.ActivityDto;
import com.jg_project.planner.activity.dto.ActivityResponse;
import com.jg_project.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActivityRespository repository;

    public ActivityResponse saveActivity(ActivityDto payload, Trip trip) {
        Activity activity = new Activity(payload.title(), payload.occurs_at(), trip);

        this.repository.save(activity);

        return new ActivityResponse(activity.getId());
    }

    public List<ActivityData> getAllActivities(UUID tripId) {
        return this.repository.findByTripId(tripId).stream().map(activities -> new ActivityData(activities.getId(),
                activities.getTitle(), activities.getOccursAt())).toList();
    }
}
