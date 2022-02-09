package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBRequirementController.class)
@Import(TestConfig.class)
public class UCSBRequirementControllerTests extends ControllerTestCase {

    @MockBean
    UCSBRequirementRepository UCSBRequirementRepository;

    @MockBean
    UserRepository userRepository;

    // Authorization tests for /api/todos/admin/all

    @Test
    public void api_UCSBRequirements_admin_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/admin/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_admin_all__user_logged_in__returns_403() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/admin/all"))
                .andExpect(status().is(403));
    }

    /*@WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_admin__user_logged_in__returns_403() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/admin?id=7"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_UCSBRequirements_admin_all__admin_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/admin/all"))
                .andExpect(status().isOk());
    }*/

    // Authorization tests for /api/todos/all

    @Test
    public void api_UCSBRequirements_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().isOk());
    }

    // Authorization tests for /api/todos/post

    @Test
    public void api_UCSBRequirements_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/UCSBRequirements/post"))
                .andExpect(status().is(403));
    }



    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_UCSBRequirements_admin_all__admin_logged_in__returns_all_UCSBRequirements() throws Exception {

        // arrange
        User u = currentUserService.getCurrentUser().getUser();

        UCSBRequirement r1 = UCSBRequirement.builder()
                .inactive(false)
                .requirementCode("a")
                .requirementTranslation("a")
                .collegeCode("a")
                .objCode("a")
                .courseCount(0)
                .units(0)
                .id(7L).build();
        UCSBRequirement r2 = UCSBRequirement.builder()
                .inactive(false)
                .requirementCode("b")
                .requirementTranslation("b")
                .collegeCode("b")
                .objCode("b")
                .courseCount(1)
                .units(1)
                .id(7L).build();

        ArrayList<UCSBRequirement> expectedUCSBRequirements = new ArrayList<>();
        expectedUCSBRequirements.addAll(Arrays.asList(r1, r2));
        when(UCSBRequirementRepository.findAll()).thenReturn(expectedUCSBRequirements);

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements/admin/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(UCSBRequirementRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedUCSBRequirements);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_user_all__user_logged_in__returns_all_UCSBRequirements() throws Exception {

        // arrange
        User u = currentUserService.getCurrentUser().getUser();

        UCSBRequirement r1 = UCSBRequirement.builder()
                .inactive(false)
                .requirementCode("a")
                .requirementTranslation("a")
                .collegeCode("a")
                .objCode("a")
                .courseCount(0)
                .units(0)
                .id(7L).build();
        UCSBRequirement r2 = UCSBRequirement.builder()
                .inactive(false)
                .requirementCode("b")
                .requirementTranslation("b")
                .collegeCode("b")
                .objCode("b")
                .courseCount(1)
                .units(1)
                .id(7L).build();

        ArrayList<UCSBRequirement> expectedUCSBRequirements = new ArrayList<>();
        expectedUCSBRequirements.addAll(Arrays.asList(r1, r2));
        when(UCSBRequirementRepository.findAll()).thenReturn(expectedUCSBRequirements);

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements/admin/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(UCSBRequirementRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedUCSBRequirements);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_post__user_logged_in() throws Exception {
        // arrange

        UCSBRequirement expectedUCSBRequirement = UCSBRequirement.builder()
                .inactive(true)
                .requirementCode("a")
                .requirementTranslation("b")
                .collegeCode("c")
                .objCode("d")
                .id(7L)
                .courseCount(6)
                .units(4)
                .build();

        when(UCSBRequirementRepository.save(eq(expectedUCSBRequirement))).thenReturn(expectedUCSBRequirement);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/UCSBRequirements/post?inactive=true&requirementCode=a&requirementTranslation=b&collegeCode=c&objCode=d&id=7&courseCount=6&unit=4")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(UCSBRequirementRepository, times(1)).save(expectedUCSBRequirement);
        String expectedJson = mapper.writeValueAsString(expectedUCSBRequirement);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
}
