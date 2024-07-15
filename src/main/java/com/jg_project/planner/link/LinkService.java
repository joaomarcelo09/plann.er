package com.jg_project.planner.link;


import com.jg_project.planner.activity.dto.ActivityData;
import com.jg_project.planner.link.dto.LinkData;
import com.jg_project.planner.link.dto.LinkDto;
import com.jg_project.planner.link.dto.LinkResponse;
import com.jg_project.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {

    @Autowired
    private LinkRepository repository;

    public LinkResponse saveLinks(LinkDto payload, Trip trip) {
        Link link = new Link(payload.title(), payload.url(), trip);

        this.repository.save(link);

        return new LinkResponse(link.getId());
    }

    public List<LinkData> getAllLinks(UUID tripId) {
        return this.repository.findByTripId(tripId).stream().map(links -> new LinkData(links.getId(),
                links.getTitle(), links.getUrl())).toList();
    }

}
