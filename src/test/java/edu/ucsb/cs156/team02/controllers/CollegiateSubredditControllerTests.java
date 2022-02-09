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

@WebMvcTest(controllers = CollegiateSubredditController.class)
@Import(TestConfig.class)
public class CollegiateSubredditControllerTests extends ControllerTestCase {

    @MockBean
    CollegiateSubredditRepository CollegiateSubredditRepository;

    @MockBean
    UserRepository userRepository;



    // Authorization tests for /api/CollegiateSubreddits/all



    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/CollegiateSubreddits/all"))
                .andExpect(status().isOk());
    }


    // Tests with mocks for database actions on user




    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddit_user_get_all() throws Exception {
        // arrange

        // User u = currentUserService.getCurrentUser().getUser();

        CollegiateSubreddit expectedSubreddit1 = CollegiateSubreddit.builder()
                .name("Name1")
                .location("Location1")
                .subreddit("Subreddit1")
                .id(0L)
                .build();
        CollegiateSubreddit expectedSubreddit2 = CollegiateSubreddit.builder()
                .name("Name2")
                .location("Location2")
                .subreddit("Subreddit2")
                .id(1L)
                .build();
        CollegiateSubreddit expectedSubreddit3 = CollegiateSubreddit.builder()
                .name("Name3")
                .location("Location3")
                .subreddit("Subreddit3")
                .id(2L)
                .build();

        ArrayList<CollegiateSubreddit> expectedSubreddits = new ArrayList<>();
        expectedSubreddits.addAll(Arrays.asList(expectedSubreddit1,expectedSubreddit2, expectedSubreddit3));

        when(CollegiateSubredditRepository.findAll()).thenReturn(expectedSubreddits);

        // act
        MvcResult response = mockMvc.perform(
                get("/api/CollegiateSubreddits/all"))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedSubreddits);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }




    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddit_user_post__user_logged_in() throws Exception {
        // arrange

        User u = currentUserService.getCurrentUser().getUser();

        CollegiateSubreddit expectedSubreddit = CollegiateSubreddit.builder()
                .name("Name1")
                .location("Location1")
                .subreddit("Subreddit1")
                .id(0L)
                .build();

        when(CollegiateSubredditRepository.save(eq(expectedSubreddit))).thenReturn(expectedSubreddit);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/CollegiateSubreddits/post?name=Name1&location=Location1&subreddit=Subreddit1")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).save(expectedSubreddit);
        String expectedJson = mapper.writeValueAsString(expectedSubreddit);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits_user_returns_a_CollegiateSubreddit_that_exists() throws Exception {

        User u = currentUserService.getCurrentUser().getUser();
        // arrange
        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder()
                .name("Name1")
                .location("Location1")
                .subreddit("Subreddit1")
                .id(5L)
                .build();
        when(CollegiateSubredditRepository.findById(eq(5L))).thenReturn(Optional.of(CollegiateSubreddit1));

        // act
        MvcResult response = mockMvc.perform(get("/api/CollegiateSubreddits?id=5"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(CollegiateSubredditRepository, times(1)).findById(eq(5L));
        String expectedJson = mapper.writeValueAsString(CollegiateSubreddit1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddits_user_search_for_CollegiateSubreddit_that_does_not_exist() throws Exception {

        User u = currentUserService.getCurrentUser().getUser();
        // arrange
        when(CollegiateSubredditRepository.findById(eq(5L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/CollegiateSubreddits?id=5"))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).findById(eq(5L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("subreddit with id 5 not found", responseString);
    }



    // Tests for Put(edit) method
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddit__put_exist() throws Exception {
        
        // arrange


        CollegiateSubreddit originalCollegiateSubreddit = CollegiateSubreddit.builder()
                .name("Name1")
                .location("Location1")
                .subreddit("Subreddit1")
                .id(67L)
                .build();

        CollegiateSubreddit updatedCollegiateSubreddit = CollegiateSubreddit.builder()
                .name("changed")
                .location("changed")
                .subreddit("changed")
                .id(67L)
                .build();
        

        String requestBody = mapper.writeValueAsString(updatedCollegiateSubreddit);
        String expectedReturn = mapper.writeValueAsString(updatedCollegiateSubreddit);

        when(CollegiateSubredditRepository.findById(eq(67L))).thenReturn(Optional.of(originalCollegiateSubreddit));

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
        verify(CollegiateSubredditRepository, times(1)).save(updatedCollegiateSubreddit); 
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddit__put_not_exist() throws Exception {
        
        // arrange

        CollegiateSubreddit updatedCollegiateSubreddit = CollegiateSubreddit.builder()
                .name("Name1")
                .location("Location1")
                .subreddit("Subreddit1")
                .id(10L)
                .build();


        String requestBody = mapper.writeValueAsString(updatedCollegiateSubreddit);

        when(CollegiateSubredditRepository.findById(eq(10L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/CollegiateSubreddits?id=10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).findById(10L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("subreddit with id 10 not found", responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddit__delete_exist() throws Exception {

        // arrange
        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder()
                .name("Name1")
                .location("Location1")
                .subreddit("Subreddit1")
                .id(15L)
                .build();


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
        assertEquals("subreddit with id 15 deleted", responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddit__delete_does_not_exist() throws Exception {
        // arrange

        User otherUser = User.builder().id(98L).build();

        CollegiateSubreddit CollegiateSubreddit1 = CollegiateSubreddit.builder()
                .name("Name1")
                .location("Location1")
                .subreddit("Subreddit1")
                .id(15L)
                .build();

        when(CollegiateSubredditRepository.findById(eq(15L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/CollegiateSubreddits?id=15")
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(CollegiateSubredditRepository, times(1)).findById(15L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("subreddit with id 15 not found", responseString);
    }



}