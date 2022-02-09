package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;
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

@Api(description = "UCSB Subjects")
@RequestMapping("/api/UCSBSubjects")
@RestController
@Slf4j
public class UCSBSubjectController extends ApiController {

    public class UCSBSubjectOrError {
        Long id;
        UCSBSubject ucsbSubject;
        ResponseEntity<String> error;

        public UCSBSubjectOrError(Long id) {
            this.id = id;
        }
    }

    @Autowired
    UCSBSubjectRepository ucsbSubjectRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all subjects in database")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public Iterable<UCSBSubject> allUCSBSubjects() {
        loggingService.logMethod();
        Iterable<UCSBSubject> ucsbSubject = ucsbSubjectRepository.findAll();
        return ucsbSubject;
    }

    @ApiOperation(value = "Get a single subject (if it belongs to current user)")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getUCSBSubjectById(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();
        UCSBSubjectOrError uoe = new UCSBSubjectOrError(id);

        uoe = doesUCSBSubjectExist(uoe);
        if (uoe.error != null) {
            return uoe.error;
        }
        /*
        uoe = doesUCSBSubjectBelongToCurrentUser(uoe);
        if (uoe.error != null) {
            return uoe.error;
        }
        */
        String body = mapper.writeValueAsString(uoe.ucsbSubject);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Update a single ucsbSubject")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public ResponseEntity<String> putTodoById(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UCSBSubject incomingUCSBSubject) throws JsonProcessingException {
        loggingService.logMethod();

        UCSBSubjectOrError uoe = new UCSBSubjectOrError(id);

        uoe = doesUCSBSubjectExist(uoe);
        if (uoe.error != null) {
            return uoe.error;
        }

        ucsbSubjectRepository.save(incomingUCSBSubject);

        String body = mapper.writeValueAsString(incomingUCSBSubject);
        return ResponseEntity.ok().body(body);
    }


    @ApiOperation(value = "Add new subject to database")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBSubject postUCSBSubject(
            @ApiParam("subjectCode") @RequestParam String subjectCode,
            @ApiParam("subjectTranslation") @RequestParam String subjectTranslation,
            @ApiParam("deptCode") @RequestParam String deptCode,
            @ApiParam("collegeCode") @RequestParam String collegeCode,
            @ApiParam("relatedDeptCode") @RequestParam String relatedDeptCode,
            @ApiParam("inactive") @RequestParam boolean inactive,
            @ApiParam("id") @RequestParam long id) {
        loggingService.logMethod();
        
        log.info("subjectCode={}", subjectCode, "subjectTranslation={}", subjectTranslation, "deptCode={}", deptCode,
         "collegeCode={}", collegeCode, "relatedDeptCode={}", relatedDeptCode, "inactive={}", inactive, "id={}", id);

        UCSBSubject ucsbSubject = new UCSBSubject();
        ucsbSubject.setSubjectCode(subjectCode);
        ucsbSubject.setSubjectTranslation(subjectTranslation);
        ucsbSubject.setDeptCode(deptCode);
        ucsbSubject.setCollegeCode(collegeCode);
        ucsbSubject.setRelatedDeptCode(relatedDeptCode);
        ucsbSubject.setInactive(inactive);
        ucsbSubject.setId(id);
        UCSBSubject savedUCSubject = ucsbSubjectRepository.save(ucsbSubject);
        return savedUCSubject;
    }

    public UCSBSubjectOrError doesUCSBSubjectExist(UCSBSubjectOrError uoe) {

        Optional<UCSBSubject> optionalUCSBSubject = ucsbSubjectRepository.findById(uoe.id);

        if (optionalUCSBSubject.isEmpty()) {
            uoe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("id %d not found", uoe.id));
        } else {
            uoe.ucsbSubject = optionalUCSBSubject.get();
        }
        return uoe;
    }

    /*
    public UCSBSubjectOrError doesUCSBSubjectBelongToCurrentUser(UCSBSubjectOrError uoe) {
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        Long currentUserId = currentUser.getUser().getId();
        Long ucsbSubjectUserId = uoe.ucsbSubject.getUser().getId();
        log.info("currentUserId={} ucsbSubjectUserId={}", currentUserId, ucsbSubjectUserId);

        if (ucsbSubjectUserId != currentUserId) {
            uoe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("ucsbSubject with id %d not found", uoe.id));
        }
        return uoe;
    }
    */

}