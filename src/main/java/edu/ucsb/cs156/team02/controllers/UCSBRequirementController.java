package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;
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

@Api(description = "UCSBRequirements")
@RequestMapping("/api/UCSBRequirements")
@RestController
@Slf4j
public class UCSBRequirementController extends ApiController {

    @Autowired
    UCSBRequirementRepository ucsbRequirementRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all UCSB Requirements")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBRequirement> allUCSBRequirements() {
        loggingService.logMethod();
        Iterable<UCSBRequirement> ucsbRequirement = ucsbRequirementRepository.findAll();
        return ucsbRequirement;
    }

    @ApiOperation(value = "Create a new UCSB Requirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBRequirement postUCSBRequirement(
            @ApiParam("id") @RequestParam long id,
            @ApiParam("requirementCode") @RequestParam String requirementCode,
            @ApiParam("requirementTranslation") @RequestParam String requirementTranslation,
            @ApiParam("collegeCode") @RequestParam String collegeCode,
            @ApiParam("objCode") @RequestParam String objCode,
            @ApiParam("courseCount") @RequestParam int courseCount,
            @ApiParam("units") @RequestParam int units,
            @ApiParam("inactive") @RequestParam boolean inactive) {
        loggingService.logMethod();

        UCSBRequirement requirement = new UCSBRequirement();
        requirement.setId(id);
        requirement.setRequirementCode(requirementCode);
        requirement.setRequirementTranslation(requirementTranslation);
        requirement.setCollegeCode(collegeCode);
        requirement.setObjCode(objCode);
        requirement.setCourseCount(courseCount);
        requirement.setUnits(units);
        requirement.setInactive(inactive);
        UCSBRequirement savedRequirements = ucsbRequirementRepository.save(requirement);
        return savedRequirements;
    }
    public class UCSBRequirementOrError {
        Long id;
        UCSBRequirement UCSBRequirement;
        ResponseEntity<String> error;

        public UCSBRequirementOrError(Long id) {
            this.id = id;
        }
    }
    @ApiOperation(value = "Get a requirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getUCSBRequirement(
        @ApiParam("id") @RequestParam Long id)
            throws JsonProcessingException{
        loggingService.logMethod();
        
        UCSBRequirementOrError toe = new UCSBRequirementOrError(id);
        toe = DoesUCSBRequirementExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

        String body = mapper.writeValueAsString(toe.UCSBRequirement);
        return ResponseEntity.ok().body(body);

    }
    public UCSBRequirementOrError DoesUCSBRequirementExist(UCSBRequirementOrError toe) {
        Optional<UCSBRequirement> optionalUCSBRequirement = UCSBRequirementRepository.findById(toe.id);
        if (optionalUCSBRequirement.isEmpty()) {
            toe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("UCSBRequirement with id %d not found", toe.id));
        } else {
            toe.UCSBRequirement = optionalUCSBRequirement.get();
        }
        return toe;
    }
    
    @ApiOperation(value = "Delete a UCSBRequirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteUCSBRequirement(
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        UCSBRequirementOrError moe = new UCSBRequirementOrError(id);

        moe = doesUCSBRequirementExist(soe);
        if (moe.error != null) {
            return moe.error;
        }

        
        UCSBRequirementRepository.deleteById(id);
        return ResponseEntity.ok().body(String.format("record %d deleted", id));

    }
    @ApiOperation(value = "Create a new Requirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBRequirement postUCSBRequirement(
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
    /*@ApiOperation(value = "Create a new requirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBrequirement postUCSBrequirement(
            @ApiParam("requirementCode") @RequestParam String requirementCode,
            @ApiParam("requirementTranslation") @RequestParam String requirementTranslation,
            @ApiParam("collegeCode") @RequestParam String deptCode,
            @ApiParam("objCode") @RequestParam String collegeCode,
            @ApiParam("courseCount") @RequestParam long, courseCount,
            @ApiParam("inactive") @RequestParam boolean inactive,
            @ApiParam("id") @RequestParam long id
            @ApiParam("units") @RequestParam long units) {

        loggingService.logMethod();
        
        log.info("requirementCode={}", requirementCode, "requirementTranslation={}", requirementTranslation, "collegeCode={}", collegeCode,
          "objCode={}", objCode, "courseCount={}", courseCount, "inactive={}", inactive, "id={}", id, "units={}", units);

        UCSBrequirement ucsbrequirement = new UCSBrequirement();
        ucsbrequirement.setrequirementCode(requirementCode);
        ucsbrequirement.setrequirementTranslation(requirementTranslation);
        ucsbrequirement.setDeptCode(deptCode);
        ucsbrequirement.setCollegeCode(collegeCode);
        ucsbrequirement.setRelatedDeptCode(relatedDeptCode);
        ucsbrequirement.setInactive(inactive);
        ucsbrequirement.setId(id);
        UCSBrequirement savedUCrequirement = ucsbrequirementRepository.save(ucsbrequirement);
        return savedUCrequirement;

        
    }*/
}

