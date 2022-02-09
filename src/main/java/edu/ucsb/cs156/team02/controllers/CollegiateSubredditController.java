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

    
    // delete put and others
    @ApiOperation(value = "Delete a CollegiateSubreddit owned by this user")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteCollegiateSubreddit(
            @ApiParam("id") @RequestParam Long id) {
        CollegiateSubredditOrError toe = new CollegiateSubredditOrError(id);

        toe = doesCollegiateSubredditExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

        toe = doesCollegiateSubredditBelongToCurrentUser(toe);
        if (toe.error != null) {
            return toe.error;
        }
        CollegiateSubredditRepository.deleteById(id);
        return ResponseEntity.ok().body(String.format("CollegiateSubreddit with id %d deleted", id));

    }

    @ApiOperation(value = "Delete another user's CollegiateSubreddit")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin")
    public ResponseEntity<String> deleteCollegiateSubreddit_Admin(
            @ApiParam("id") @RequestParam Long id) {
        CollegiateSubredditOrError toe = new CollegiateSubredditOrError(id);

        toe = doesCollegiateSubredditExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

        CollegiateSubredditRepository.deleteById(id);

        return ResponseEntity.ok().body(String.format("CollegiateSubreddit with id %d deleted", id));

    }

    @ApiOperation(value = "Update a single CollegiateSubreddit (if it belongs to current user)")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public ResponseEntity<String> putCollegiateSubredditById(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid CollegiateSubreddit incomingCollegiateSubreddit) throws JsonProcessingException {
        CurrentUser currentUser = getCurrentUser();
        User user = currentUser.getUser();

        CollegiateSubredditOrError toe = new CollegiateSubredditOrError(id);

        toe = doesCollegiateSubredditExist(toe);
        if (toe.error != null) {
            return toe.error;
        }
        toe = doesCollegiateSubredditBelongToCurrentUser(toe);
        if (toe.error != null) {
            return toe.error;
        }

        incomingCollegiateSubreddit.setUser(user);
        CollegiateSubredditRepository.save(incomingCollegiateSubreddit);

        String body = mapper.writeValueAsString(incomingCollegiateSubreddit);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Update a single CollegiateSubreddit (regardless of ownership, admin only, can't change ownership)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/admin")
    public ResponseEntity<String> putCollegiateSubredditById_admin(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid CollegiateSubreddit incomingCollegiateSubreddit) throws JsonProcessingException {
        CollegiateSubredditOrError toe = new CollegiateSubredditOrError(id);

        toe = doesCollegiateSubredditExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

        // Even the admin can't change the user; they can change other details
        // but not that.

        User previousUser = toe.CollegiateSubreddit.getUser();
        incomingCollegiateSubreddit.setUser(previousUser);
        CollegiateSubredditRepository.save(incomingCollegiateSubreddit);

        String body = mapper.writeValueAsString(incomingCollegiateSubreddit);
        return ResponseEntity.ok().body(body);
    }

    /**
     * Pre-conditions: toe.id is value to look up, toe.CollegiateSubreddit and toe.error are null
     *
     * Post-condition: if CollegiateSubreddit with id toe.id exists, toe.CollegiateSubreddit now refers to it, and
     * error is null.
     * Otherwise, CollegiateSubreddit with id toe.id does not exist, and error is a suitable return
     * value to
     * report this error condition.
     */
    public CollegiateSubredditOrError doesCollegiateSubredditExist(CollegiateSubredditOrError toe) {

        Optional<CollegiateSubreddit> optionalCollegiateSubreddit = CollegiateSubredditRepository.findById(toe.id);

        if (optionalCollegiateSubreddit.isEmpty()) {
            toe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("CollegiateSubreddit with id %d not found", toe.id));
        } else {
            toe.CollegiateSubreddit = optionalCollegiateSubreddit.get();
        }
        return toe;
    }

    /**
     * Pre-conditions: toe.CollegiateSubreddit is non-null and refers to the CollegiateSubreddit with id toe.id,
     * and toe.error is null
     *
     * Post-condition: if CollegiateSubreddit belongs to current user, then error is still null.
     * Otherwise error is a suitable
     * return value.
     */
    public CollegiateSubredditOrError doesCollegiateSubredditBelongToCurrentUser(CollegiateSubredditOrError toe) {
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        Long currentUserId = currentUser.getUser().getId();
        Long CollegiateSubredditUserId = toe.CollegiateSubreddit.getUser().getId();
        log.info("currentUserId={} CollegiateSubredditUserId={}", currentUserId, CollegiateSubredditUserId);

        if (CollegiateSubredditUserId != currentUserId) {
            toe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("CollegiateSubreddit with id %d not found", toe.id));
        }
        return toe;
    }

}



    


    

}