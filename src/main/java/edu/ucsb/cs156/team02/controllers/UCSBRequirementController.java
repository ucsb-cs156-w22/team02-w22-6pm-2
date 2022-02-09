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
    public Iterable<UCSBRequirement> allUsersTodos() {
        loggingService.logMethod();
        Iterable<UCSBRequirement> ucsbRequirement = ucsbRequirementRepository.findAll();
        return ucsbRequirement;
    }

    @ApiOperation(value = "Create a new UCSB Requirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBRequirement postTodo(
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
}

