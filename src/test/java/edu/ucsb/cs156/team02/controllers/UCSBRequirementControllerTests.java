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

   /* @Test
    public void api_UCSBRequirements_admin_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/admin/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_admin_all__user_logged_in__returns_403() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/admin/all"))
                .andExpect(status().is(403));
    }*/

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

    /*@Test
    public void api_UCSBRequirements_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().is(403));
    }*/

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().isOk());
    }

    // Authorization tests for /api/todos/post

    /*@Test
    public void api_UCSBRequirements_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/UCSBRequirements/post"))
                .andExpect(status().is(403));
    }*/



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

     @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_user_returns_a_UCSBRequirement_that_exists() throws Exception {

        User u = currentUserService.getCurrentUser().getUser();
        // arrange
        UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder()
        .inactive(true)
        .requirementCode("a")
        .requirementTranslation("b")
        .collegeCode("c")
        .objCode("d")
        .id(7L)
        .courseCount(6)
        .units(4)
        .build();
        when(UCSBRequirementRepository.findById(eq(5L))).thenReturn(Optional.of(UCSBRequirement1));

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=5"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(UCSBRequirementRepository, times(1)).findById(eq(5L));
        String expectedJson = mapper.writeValueAsString(UCSBRequirement1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_user_search_for_UCSBRequirement_that_does_not_exist() throws Exception {

        User u = currentUserService.getCurrentUser().getUser();
        // arrange
        when(UCSBRequirementRepository.findById(eq(5L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=5"))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(UCSBRequirementRepository, times(1)).findById(eq(5L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("requirement with id 5 not found", responseString);
    }



    // Tests for Put(edit) method
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirement__put_exist() throws Exception {
        
        // arrange


        UCSBRequirement originalUCSBRequirement = UCSBRequirement.builder()
        .inactive(true)
        .requirementCode("a")
        .requirementTranslation("b")
        .collegeCode("c")
        .objCode("d")
        .id(7L)
        .courseCount(6)
        .units(4)
        .build();

        UCSBRequirement updatedUCSBRequirement = UCSBRequirement.builder()
        .inactive(true)
        .requirementCode("d")
        .requirementTranslation("e")
        .collegeCode("d")
        .objCode("d")
        .id(7L)
        .courseCount(7)
        .units(4)
        .build();
        

        String requestBody = mapper.writeValueAsString(updatedUCSBRequirement);
        String expectedReturn = mapper.writeValueAsString(updatedUCSBRequirement);

        when(UCSBRequirementRepository.findById(eq(67L))).thenReturn(Optional.of(originalUCSBRequirement));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBRequirements?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(UCSBRequirementRepository, times(1)).findById(67L);
        verify(UCSBRequirementRepository, times(1)).save(updatedUCSBRequirement); 
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirement__put_not_exist() throws Exception {
        
        // arrange

        UCSBRequirement updatedUCSBRequirement = UCSBRequirement.builder()
        .inactive(true)
        .requirementCode("a")
        .requirementTranslation("b")
        .collegeCode("c")
        .objCode("d")
        .id(7L)
        .courseCount(6)
        .units(4)
        .build();


        String requestBody = mapper.writeValueAsString(updatedUCSBRequirement);

        when(UCSBRequirementRepository.findById(eq(10L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBRequirements?id=10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(UCSBRequirementRepository, times(1)).findById(10L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("requirement with id 10 not found", responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirement__delete_exist() throws Exception {

        // arrange
        UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder()
        .inactive(true)
        .requirementCode("a")
        .requirementTranslation("b")
        .collegeCode("c")
        .objCode("d")
        .id(7L)
        .courseCount(6)
        .units(4)
        .build();


        when(UCSBRequirementRepository.findById(eq(15L))).thenReturn(Optional.of(UCSBRequirement1));


        // act
        MvcResult response = mockMvc.perform(
                delete("/api/UCSBRequirements?id=15")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(UCSBRequirementRepository, times(1)).findById(15L);
        verify(UCSBRequirementRepository, times(1)).deleteById(15L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("requirement with id 15 deleted", responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirement__delete_does_not_exist() throws Exception {
        // arrange

        User otherUser = User.builder().id(98L).build();

        UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder()
        .inactive(true)
        .requirementCode("a")
        .requirementTranslation("b")
        .collegeCode("c")
        .objCode("d")
        .id(7L)
        .courseCount(6)
        .units(4)
        .build();

        when(UCSBRequirementRepository.findById(eq(15L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/UCSBRequirements?id=15")
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(UCSBRequirementRepository, times(1)).findById(15L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("requirement with id 15 not found", responseString);
    }
}
