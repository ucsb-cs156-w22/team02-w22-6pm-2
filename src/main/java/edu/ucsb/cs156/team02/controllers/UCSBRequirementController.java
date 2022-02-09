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

    public class UCSBRequirementOrError {
        Long id;
        UCSBRequirement UCSBRequirement;
        ResponseEntity<String> error;

        public UCSBRequirementOrError(Long id) {
            this.id = id;
        }
    }
    public UCSBRequirementOrError DoesUCSBRequirementExist(UCSBRequirementOrError toe) {
        Optional<UCSBRequirement> optionalUCSBRequirement = ucsbRequirementRepository.findById(toe.id);
        if (optionalUCSBRequirement.isEmpty()) {
            toe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("UCSBRequirement with id %d not found", toe.id));
        } else {
            toe.UCSBRequirement = optionalUCSBRequirement.get();
        }
        return toe;
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
    
    @ApiOperation(value = "Update a single requirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public ResponseEntity<String> putUCSBRequirementtById(
        @ApiParam("id") @RequestParam Long id,
        @RequestBody @Valid UCSBRequirement incomingUCSBRequirement) throws JsonProcessingException {
            loggingService.logMethod();
            
            UCSBRequirementOrError coe = new UCSBRequirementOrError(id);
            
            coe = DoesUCSBRequirementExist(coe);
            if (coe.error != null) {
                return coe.error;
            }
        UCSBRequirement oldRequirement = coe.UCSBRequirement;
        oldRequirement.setRequirementCode(incomingUCSBRequirement.getRequirementCode());
        oldRequirement.setRequirementTranslation(incomingUCSBRequirement.getRequirementTranslation());
        oldRequirement.setObjCode(incomingUCSBRequirement.getObjCode());
        oldRequirement.setCollegeCode(incomingUCSBRequirement.getCollegeCode());
        oldRequirement.setCourseCount(incomingUCSBRequirement.getCourseCount());
        oldRequirement.setInactive(incomingUCSBRequirement.isInactive());
        oldRequirement.setId(incomingUCSBRequirement.getId());
        oldRequirement.setUnits(incomingUCSBRequirement.getUnits());
        
        ucsbRequirementRepository.save(oldRequirement);
        
        String body = mapper.writeValueAsString(oldRequirement);
        return ResponseEntity.ok().body(body);
    }

    
    @ApiOperation(value = "Delete a UCSBRequirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteUCSBRequirement(
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        UCSBRequirementOrError moe = new UCSBRequirementOrError(id);

        moe = DoesUCSBRequirementExist(moe);
        if (moe.error != null) {
            return moe.error;
        }

        
        ucsbRequirementRepository.deleteById(id);
        return ResponseEntity.ok().body(String.format("record %d deleted", id));

    }
    
    @ApiOperation(value = "Create a new requirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBRequirement postUCSBRequirement(
            @ApiParam("requirementCode") @RequestParam String requirementCode,
            @ApiParam("requirementTranslation") @RequestParam String requirementTranslation,
            @ApiParam("collegeCode") @RequestParam String collegeCode,
            @ApiParam("objCode") @RequestParam String objCode,
            @ApiParam("courseCount") @RequestParam int courseCount,
            @ApiParam("inactive") @RequestParam boolean inactive,
            @ApiParam("id") @RequestParam long id,
            @ApiParam("units") @RequestParam int units) {

        loggingService.logMethod();
        
        log.info("requirementCode={}", requirementCode, "requirementTranslation={}", requirementTranslation, "collegeCode={}", collegeCode,
          "objCode={}", objCode, "courseCount={}", courseCount, "inactive={}", inactive, "id={}", id, "units={}", units);

        UCSBRequirement UCSBrequirement = new UCSBRequirement();
        UCSBrequirement.setRequirementCode(requirementCode);
        UCSBrequirement.setRequirementTranslation(requirementTranslation);
        UCSBrequirement.setObjCode(objCode);
        UCSBrequirement.setCollegeCode(collegeCode);
        UCSBrequirement.setCourseCount(courseCount);
        UCSBrequirement.setInactive(inactive);
        UCSBrequirement.setId(id);
        UCSBrequirement.setUnits(units);
        UCSBRequirement savedUCrequirement = ucsbRequirementRepository.save(UCSBrequirement);
        return savedUCrequirement;

        
    }
}

