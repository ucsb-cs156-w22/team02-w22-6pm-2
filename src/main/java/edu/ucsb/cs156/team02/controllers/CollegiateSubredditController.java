package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.CollegiateSubreddit;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.CollegiateSubredditRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Api(description = "CollegiateSubreddit")
@RequestMapping("/api/CollegiateSubreddits")
@RestController
@Slf4j
public class CollegiateSubredditController extends ApiController {

    

    @Autowired
    CollegiateSubredditRepository CollegiateSubredditRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all CollegiateSubreddits")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public Iterable<CollegiateSubreddit> allCollegiateSubreddits() {
        loggingService.logMethod();
        Iterable<CollegiateSubreddit> CollegiateSubreddits = CollegiateSubredditRepository.findAll();
        return CollegiateSubreddits;
    }


    @ApiOperation(value = "Create a new CollegiateSubreddit")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public CollegiateSubreddit postCollegiateSubreddit(
            @ApiParam("name") @RequestParam String name,
            @ApiParam("location") @RequestParam String location,
            @ApiParam("subreddit") @RequestParam String subreddit,
            @ApiParam("id") @RequestParam long id) {
        loggingService.logMethod();
        
        log.info("name={}", name, "location={}", location, "subreddit={}", subreddit, "id={}", id);

        CollegiateSubreddit CollegiateSubreddit = new CollegiateSubreddit();
        CollegiateSubreddit.setName(name);
        CollegiateSubreddit.setLocation(location);
        CollegiateSubreddit.setSubreddit(subreddit);
        CollegiateSubreddit.setId(id);
        CollegiateSubreddit savedCollegiateSubreddit = CollegiateSubredditRepository.save(CollegiateSubreddit);
        return savedCollegiateSubreddit;

        
    }



    


    

}