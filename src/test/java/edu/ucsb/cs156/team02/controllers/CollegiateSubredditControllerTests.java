package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.CollegiateSubreddit;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.CollegiateSubredditRepository;

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

@WebMvcTest(controllers = CollegiateSubredditsController.class)
@Import(TestConfig.class)
public class CollegiateSubredditsControllerTests extends ControllerTestCase {

    @MockBean
    CollegiateSubredditRepository CollegiateSubredditRepository;

    @MockBean
    UserRepository userRepository;

    // Authorization tests for /api/CollegiateSubreddits/admin/all

    @Test
    public void api_CollegiateSubreddits_admin_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/CollegiateSubreddits/admin/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits_admin_all__user_logged_in__returns_403() throws Exception {
        mockMvc.perform(get("/api/CollegiateSubreddits/admin/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits_admin__user_logged_in__returns_403() throws Exception {
        mockMvc.perform(get("/api/CollegiateSubreddits/admin?id=7"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_CollegiateSubreddits_admin_all__admin_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/CollegiateSubreddits/admin/all"))
                .andExpect(status().isOk());
    }

    // Authorization tests for /api/CollegiateSubreddits/all

    @Test
    public void api_CollegiateSubreddits_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/CollegiateSubreddits/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/CollegiateSubreddits/all"))
                .andExpect(status().isOk());
    }

    // Authorization tests for /api/CollegiateSubreddits/post

    @Test
    public void api_CollegiateSubreddits_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/CollegiateSubreddits/post"))
                .andExpect(status().is(403));
    }

    // Tests with mocks for database actions

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits__user_logged_in__returns_a_CollegiateSubreddit_that_exists() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder().title("CollegiateSubreddit 1").details("CollegiateSubreddit 1").done(false).user(u).id(7L).build();
        when(CollegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.of(CollegiateSubreddit1));

        // act
        MvcResult response = mockMvc.perform(get("/api/CollegiateSubreddits?id=7"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(CollegiateSubredditRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(CollegiateSubreddit1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits__user_logged_in__search_for_CollegiateSubreddit_that_does_not_exist() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();

        when(CollegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/CollegiateSubreddits?id=7"))
                .andExpect(status().isBadRequest()).andReturn();

        // assert

        verify(CollegiateSubredditRepository, times(1)).findById(eq(7L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 7 not found", responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits__user_logged_in__search_for_CollegiateSubreddit_that_belongs_to_another_user() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        User otherUser = User.builder().id(999L).build();
        CollegiateSubreddit otherUsersCollegiateSubreddit = CollegiateSubreddit.builder().title("CollegiateSubreddit 1").details("CollegiateSubreddit 1").done(false).user(otherUser).id(13L)
                .build();

        when(CollegiateSubredditRepository.findById(eq(13L))).thenReturn(Optional.of(otherUsersCollegiateSubreddit));

        // act
        MvcResult response = mockMvc.perform(get("/api/CollegiateSubreddits?id=13"))
                .andExpect(status().isBadRequest()).andReturn();

        // assert

        verify(CollegiateSubredditRepository, times(1)).findById(eq(13L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 13 not found", responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_CollegiateSubreddits__admin_logged_in__search_for_CollegiateSubreddit_that_belongs_to_another_user() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        User otherUser = User.builder().id(999L).build();
        CollegiateSubreddit otherUsersCollegiateSubreddit = CollegiateSubreddit.builder().title("CollegiateSubreddit 1").details("CollegiateSubreddit 1").done(false).user(otherUser).id(27L)
                .build();

        when(CollegiateSubredditRepository.findById(eq(27L))).thenReturn(Optional.of(otherUsersCollegiateSubreddit));

        // act
        MvcResult response = mockMvc.perform(get("/api/CollegiateSubreddits/admin?id=27"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(CollegiateSubredditRepository, times(1)).findById(eq(27L));
        String expectedJson = mapper.writeValueAsString(otherUsersCollegiateSubreddit);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_CollegiateSubreddits__admin_logged_in__search_for_CollegiateSubreddit_that_does_not_exist() throws Exception {

        // arrange

        when(CollegiateSubredditRepository.findById(eq(29L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/CollegiateSubreddits/admin?id=29"))
                .andExpect(status().isBadRequest()).andReturn();

        // assert

        verify(CollegiateSubredditRepository, times(1)).findById(eq(29L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 29 not found", responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_CollegiateSubreddits_admin_all__admin_logged_in__returns_all_CollegiateSubreddits() throws Exception {

        // arrange

        User u1 = User.builder().id(1L).build();
        User u2 = User.builder().id(2L).build();
        User u = currentUserService.getCurrentUser().getUser();

        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder().title("CollegiateSubreddit 1").details("CollegiateSubreddit 1").done(false).user(u1).id(1L).build();
        CollegiateSubreddit CollegiateSubreddit2 = CollegiateSubreddit.builder().title("CollegiateSubreddit 2").details("CollegiateSubreddit 2").done(false).user(u2).id(2L).build();
        CollegiateSubreddit CollegiateSubreddit3 = CollegiateSubreddit.builder().title("CollegiateSubreddit 3").details("CollegiateSubreddit 3").done(false).user(u).id(3L).build();

        ArrayList<CollegiateSubreddit> expectedCollegiateSubreddits = new ArrayList<>();
        expectedCollegiateSubreddits.addAll(Arrays.asList(CollegiateSubreddit1, CollegiateSubreddit2, CollegiateSubreddit3));

        when(CollegiateSubredditRepository.findAll()).thenReturn(expectedCollegiateSubreddits);

        // act
        MvcResult response = mockMvc.perform(get("/api/CollegiateSubreddits/admin/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(CollegiateSubredditRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedCollegiateSubreddits);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits_all__user_logged_in__returns_only_CollegiateSubreddits_for_user() throws Exception {

        // arrange

        User thisUser = currentUserService.getCurrentUser().getUser();

        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder().title("CollegiateSubreddit 1").details("CollegiateSubreddit 1").done(false).user(thisUser).id(1L).build();
        CollegiateSubreddit CollegiateSubreddit2 = CollegiateSubreddit.builder().title("CollegiateSubreddit 2").details("CollegiateSubreddit 2").done(false).user(thisUser).id(2L).build();

        ArrayList<CollegiateSubreddit> expectedCollegiateSubreddits = new ArrayList<>();
        expectedCollegiateSubreddits.addAll(Arrays.asList(CollegiateSubreddit1, CollegiateSubreddit2));
        when(CollegiateSubredditRepository.findAllByUserId(thisUser.getId())).thenReturn(expectedCollegiateSubreddits);

        // act
        MvcResult response = mockMvc.perform(get("/api/CollegiateSubreddits/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(CollegiateSubredditRepository, times(1)).findAllByUserId(eq(thisUser.getId()));
        String expectedJson = mapper.writeValueAsString(expectedCollegiateSubreddits);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits_post__user_logged_in() throws Exception {
        // arrange

        User u = currentUserService.getCurrentUser().getUser();

        CollegiateSubreddit expectedCollegiateSubreddit = CollegiateSubreddit.builder()
                .title("Test Title")
                .details("Test Details")
                .done(true)
                .user(u)
                .id(0L)
                .build();

        when(CollegiateSubredditRepository.save(eq(expectedCollegiateSubreddit))).thenReturn(expectedCollegiateSubreddit);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/CollegiateSubreddits/post?title=Test Title&details=Test Details&done=true")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).save(expectedCollegiateSubreddit);
        String expectedJson = mapper.writeValueAsString(expectedCollegiateSubreddit);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits__user_logged_in__delete_CollegiateSubreddit() throws Exception {
        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder().title("CollegiateSubreddit 1").details("CollegiateSubreddit 1").done(false).user(u).id(15L).build();
        when(CollegiateSubredditRepository.findById(eq(15L))).thenReturn(Optional.of(CollegiateSubreddit1));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/CollegiateSubreddits?id=15")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).findById(15L);
        verify(CollegiateSubredditRepository, times(1)).deleteById(15L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 15 deleted", responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits__user_logged_in__delete_CollegiateSubreddit_that_does_not_exist() throws Exception {
        // arrange

        User otherUser = User.builder().id(98L).build();
        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder().title("CollegiateSubreddit 1").details("CollegiateSubreddit 1").done(false).user(otherUser).id(15L).build();
        when(CollegiateSubredditRepository.findById(eq(15L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/CollegiateSubreddits?id=15")
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).findById(15L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 15 not found", responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits__user_logged_in__cannot_delete_CollegiateSubreddit_belonging_to_another_user() throws Exception {
        // arrange

        User otherUser = User.builder().id(98L).build();
        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder().title("CollegiateSubreddit 1").details("CollegiateSubreddit 1").done(false).user(otherUser).id(31L).build();
        when(CollegiateSubredditRepository.findById(eq(31L))).thenReturn(Optional.of(CollegiateSubreddit1));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/CollegiateSubreddits?id=31")
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).findById(31L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 31 not found", responseString);
    }


    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_CollegiateSubreddits__admin_logged_in__delete_CollegiateSubreddit() throws Exception {
        // arrange

        User otherUser = User.builder().id(98L).build();
        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder().title("CollegiateSubreddit 1").details("CollegiateSubreddit 1").done(false).user(otherUser).id(16L).build();
        when(CollegiateSubredditRepository.findById(eq(16L))).thenReturn(Optional.of(CollegiateSubreddit1));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/CollegiateSubreddits/admin?id=16")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).findById(16L);
        verify(CollegiateSubredditRepository, times(1)).deleteById(16L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 16 deleted", responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_CollegiateSubreddits__admin_logged_in__cannot_delete_CollegiateSubreddit_that_does_not_exist() throws Exception {
        // arrange

        when(CollegiateSubredditRepository.findById(eq(17L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/CollegiateSubreddits/admin?id=17")
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).findById(17L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 17 not found", responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits__user_logged_in__put_CollegiateSubreddit() throws Exception {
        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        User otherUser = User.builder().id(999).build();
        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder().title("CollegiateSubreddit 1").details("CollegiateSubreddit 1").done(false).user(u).id(67L).build();
        // We deliberately set the user information to another user
        // This shoudl get ignored and overwritten with currrent user when CollegiateSubreddit is saved

        CollegiateSubreddit updatedCollegiateSubreddit = CollegiateSubreddit.builder().title("New Title").details("New Details").done(true).user(otherUser).id(67L).build();
        CollegiateSubreddit correctCollegiateSubreddit = CollegiateSubreddit.builder().title("New Title").details("New Details").done(true).user(u).id(67L).build();

        String requestBody = mapper.writeValueAsString(updatedCollegiateSubreddit);
        String expectedReturn = mapper.writeValueAsString(correctCollegiateSubreddit);

        when(CollegiateSubredditRepository.findById(eq(67L))).thenReturn(Optional.of(CollegiateSubreddit1));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/CollegiateSubreddits?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).findById(67L);
        verify(CollegiateSubredditRepository, times(1)).save(correctCollegiateSubreddit); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits__user_logged_in__cannot_put_CollegiateSubreddit_that_does_not_exist() throws Exception {
        // arrange

        CollegiateSubreddit updatedCollegiateSubreddit = CollegiateSubreddit.builder().title("New Title").details("New Details").done(true).id(67L).build();

        String requestBody = mapper.writeValueAsString(updatedCollegiateSubreddit);

        when(CollegiateSubredditRepository.findById(eq(67L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/CollegiateSubreddits?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).findById(67L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 67 not found", responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits__user_logged_in__cannot_put_CollegiateSubreddit_for_another_user() throws Exception {
        // arrange

        User otherUser = User.builder().id(98L).build();
        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder().title("CollegiateSubreddit 1").details("CollegiateSubreddit 1").done(false).user(otherUser).id(31L).build();
        CollegiateSubreddit updatedCollegiateSubreddit = CollegiateSubreddit.builder().title("New Title").details("New Details").done(true).id(31L).build();

        when(CollegiateSubredditRepository.findById(eq(31L))).thenReturn(Optional.of(CollegiateSubreddit1));

        String requestBody = mapper.writeValueAsString(updatedCollegiateSubreddit);

        when(CollegiateSubredditRepository.findById(eq(67L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/CollegiateSubreddits?id=31")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).findById(31L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 31 not found", responseString);
    }


    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_CollegiateSubreddits__admin_logged_in__put_CollegiateSubreddit() throws Exception {
        // arrange

        User otherUser = User.builder().id(255L).build();
        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder().title("CollegiateSubreddit 1").details("CollegiateSubreddit 1").done(false).user(otherUser).id(77L).build();
        User yetAnotherUser = User.builder().id(512L).build();
        // We deliberately put the wrong user on the updated CollegiateSubreddit
        // We expect the controller to ignore this and keep the user the same
        CollegiateSubreddit updatedCollegiateSubreddit = CollegiateSubreddit.builder().title("New Title").details("New Details").done(true).user(yetAnotherUser).id(77L)
                .build();
        CollegiateSubreddit correctCollegiateSubreddit = CollegiateSubreddit.builder().title("New Title").details("New Details").done(true).user(otherUser).id(77L)
                .build();

        String requestBody = mapper.writeValueAsString(updatedCollegiateSubreddit);
        String expectedJson = mapper.writeValueAsString(correctCollegiateSubreddit);

        when(CollegiateSubredditRepository.findById(eq(77L))).thenReturn(Optional.of(CollegiateSubreddit1));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/CollegiateSubreddits/admin?id=77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).findById(77L);
        verify(CollegiateSubredditRepository, times(1)).save(correctCollegiateSubreddit);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_CollegiateSubreddits__admin_logged_in__cannot_put_CollegiateSubreddit_that_does_not_exist() throws Exception {
        // arrange

        User otherUser = User.builder().id(345L).build();
        CollegiateSubreddit updatedCollegiateSubreddit = CollegiateSubreddit.builder().title("New Title").details("New Details").done(true).user(otherUser).id(77L)
                .build();

        String requestBody = mapper.writeValueAsString(updatedCollegiateSubreddit);

        when(CollegiateSubredditRepository.findById(eq(77L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/CollegiateSubreddits/admin?id=77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).findById(77L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 77 not found", responseString);
    }

}
