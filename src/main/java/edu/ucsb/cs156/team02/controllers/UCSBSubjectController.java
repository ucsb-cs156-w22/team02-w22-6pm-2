package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.TodoRepository;
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

    

    @Autowired
    UCSBSubjectRepository ucsbSubjectRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all subjects")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public Iterable<UCSBSubject> allUCSBSubjects() {
        loggingService.logMethod();
        Iterable<UCSBSubject> ucsbSubject = ucsbSubjectRepository.findAll();
        return ucsbSubject;
    }


    public class UCSBSubjectOrError {
        Long id;
        UCSBSubject ucsbSubject;
        ResponseEntity<String> error;

        public UCSBSubjectOrError(Long id) {
            this.id = id;
        }
    }

    public UCSBSubjectOrError doesUCSBSubjectExist(UCSBSubjectOrError soe) {

        Optional<UCSBSubject> optionalUCSBSubject = ucsbSubjectRepository.findById(soe.id);

        if (optionalUCSBSubject.isEmpty()) {
            soe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("UCSBSubject with id %d not found", soe.id));
        } else {
            soe.ucsbSubject = optionalUCSBSubject.get();
        }
        return soe;
    }

    @ApiOperation(value = "Get a single UCSBSubject with id 123")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getUCSBSubjectById(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();
        UCSBSubjectOrError soe = new UCSBSubjectOrError(id);

        soe = doesUCSBSubjectExist(soe);
        if (soe.error != null) {
            return soe.error;
        }
        
        String body = mapper.writeValueAsString(soe.ucsbSubject);
        return ResponseEntity.ok().body(body);
    }




    @ApiOperation(value = "Create a new Subject")
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



    


    

}