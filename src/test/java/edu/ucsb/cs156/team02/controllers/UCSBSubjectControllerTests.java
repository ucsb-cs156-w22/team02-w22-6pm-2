package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;

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

@WebMvcTest(controllers = UCSBSubjectController.class)
@Import(TestConfig.class)
public class UCSBSubjectControllerTests extends ControllerTestCase {

    @MockBean
    UCSBSubjectRepository UCSBSubjectRepository;

    @MockBean
    UserRepository userRepository;



    // Authorization tests for /api/UCSBSubjects/all



    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubjects_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/UCSBSubjects/all"))
                .andExpect(status().isOk());
    }


    // Tests with mocks for database actions on user




    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject_user_get_all() throws Exception {
        // arrange

        // User u = currentUserService.getCurrentUser().getUser();

        UCSBSubject expectedSubject1 = UCSBSubject.builder()
                .subjectCode("a")
                .subjectTranslation("a")
                .collegeCode("a")
                .deptCode("a")
                .collegeCode("a")
                .relatedDeptCode("a")
                .inactive(false)
                .id(0L).build();
        UCSBSubject expectedSubject2 = UCSBSubject.builder()
                .subjectCode("b")
                .subjectTranslation("b")
                .collegeCode("b")
                .deptCode("b")
                .collegeCode("b")
                .relatedDeptCode("b")
                .inactive(false)
                .id(1L).build();
        UCSBSubject expectedSubject3 = UCSBSubject.builder()
                .subjectCode("c")
                .subjectTranslation("c")
                .collegeCode("c")
                .deptCode("c")
                .collegeCode("c")
                .relatedDeptCode("c")
                .inactive(false)
                .id(2L).build();

        ArrayList<UCSBSubject> expectedSubjects = new ArrayList<>();
        expectedSubjects.addAll(Arrays.asList(expectedSubject1,expectedSubject2, expectedSubject3));

        when(UCSBSubjectRepository.findAll()).thenReturn(expectedSubjects);

        // act
        MvcResult response = mockMvc.perform(
                get("/api/UCSBSubjects/all"))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(UCSBSubjectRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedSubjects);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }




    @WithMockUser(roles = { "USER","ADMIN" })
    @Test
    public void api_UCSBSubject_user_post__user_logged_in() throws Exception {
        UCSBSubject expectedUCSBSubject = UCSBSubject.builder()
        .subjectCode("a")
        .subjectTranslation("b")
        .collegeCode("c")
        .deptCode("d")
        .collegeCode("e")
        .relatedDeptCode("f")
        .inactive(false)
        .id(0L).build();

        when(UCSBSubjectRepository.save(eq(expectedUCSBSubject))).thenReturn(expectedUCSBSubject);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/UCSBSubjects/post?subjectCode=a&subjectTranslation=b&deptCode=d&collegeCode=e&relatedDeptCode=f&inactive=false")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(UCSBSubjectRepository, times(1)).save(expectedUCSBSubject);
        String expectedJson = mapper.writeValueAsString(expectedUCSBSubject);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);

        
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubjects_user_returns_a_UCSBSubject_that_exists() throws Exception {

        User u = currentUserService.getCurrentUser().getUser();
        // arrange
        UCSBSubject UCSBSubject1 = UCSBSubject.builder()
        .subjectCode("a")
        .subjectTranslation("b")
        .collegeCode("c")
        .deptCode("d")
        .collegeCode("e")
        .relatedDeptCode("f")
        .inactive(false)
        .id(5L).build();
        when(UCSBSubjectRepository.findById(eq(5L))).thenReturn(Optional.of(UCSBSubject1));

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects?id=5"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(UCSBSubjectRepository, times(1)).findById(eq(5L));
        String expectedJson = mapper.writeValueAsString(UCSBSubject1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubjects_user_search_for_UCSBSubject_that_does_not_exist() throws Exception {

        User u = currentUserService.getCurrentUser().getUser();
        // arrange
        when(UCSBSubjectRepository.findById(eq(5L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects?id=5"))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(UCSBSubjectRepository, times(1)).findById(eq(5L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("UCSBSubject with id 5 not found", responseString);
    }



    // Tests for Put(edit) method
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject__put_exist() throws Exception {
        
        // arrange


        UCSBSubject originalUCSBSubject = UCSBSubject.builder()
        .subjectCode("a")
        .subjectTranslation("b")
        .collegeCode("c")
        .deptCode("d")
        .collegeCode("e")
        .relatedDeptCode("f")
        .inactive(false)
        .id(67L).build();

        UCSBSubject updatedUCSBSubject = UCSBSubject.builder()
        .subjectCode("d")
        .subjectTranslation("f")
        .collegeCode("c")
        .deptCode("d")
        .collegeCode("f")
        .relatedDeptCode("f")
        .inactive(false)
        .id(67L).build();
        

        String requestBody = mapper.writeValueAsString(updatedUCSBSubject);
        String expectedReturn = mapper.writeValueAsString(updatedUCSBSubject);

        when(UCSBSubjectRepository.findById(eq(67L))).thenReturn(Optional.of(originalUCSBSubject));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBSubjects?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(UCSBSubjectRepository, times(1)).findById(67L);
        verify(UCSBSubjectRepository, times(1)).save(updatedUCSBSubject); 
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject__put_not_exist() throws Exception {
        
        // arrange

        UCSBSubject updatedUCSBSubject = UCSBSubject.builder()
        .subjectCode("d")
        .subjectTranslation("f")
        .collegeCode("c")
        .deptCode("d")
        .collegeCode("f")
        .relatedDeptCode("f")
        .inactive(false)
        .id(10L).build();


        String requestBody = mapper.writeValueAsString(updatedUCSBSubject);

        when(UCSBSubjectRepository.findById(eq(10L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBSubjects?id=10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(UCSBSubjectRepository, times(1)).findById(10L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("UCSBSubject with id 10 not found", responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject__delete_exist() throws Exception {

        // arrange
        UCSBSubject UCSBSubject1 = UCSBSubject.builder()
        .subjectCode("d")
        .subjectTranslation("f")
        .collegeCode("c")
        .deptCode("d")
        .collegeCode("f")
        .relatedDeptCode("f")
        .inactive(false)
        .id(15L).build();


        when(UCSBSubjectRepository.findById(eq(15L))).thenReturn(Optional.of(UCSBSubject1));


        // act
        MvcResult response = mockMvc.perform(
                delete("/api/UCSBSubjects?id=15")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(UCSBSubjectRepository, times(1)).findById(15L);
        verify(UCSBSubjectRepository, times(1)).deleteById(15L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("UCSBSubject with id 15 deleted", responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBSubject__delete_does_not_exist() throws Exception {
        // arrange

        User otherUser = User.builder().id(98L).build();

        UCSBSubject UCSBSubject1 = UCSBSubject.builder()
        .subjectCode("d")
        .subjectTranslation("f")
        .collegeCode("c")
        .deptCode("d")
        .collegeCode("f")
        .relatedDeptCode("f")
        .inactive(false)
        .id(15L).build();

        when(UCSBSubjectRepository.findById(eq(15L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/UCSBSubjects?id=15")
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(UCSBSubjectRepository, times(1)).findById(15L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("UCSBSubject with id 15 not found", responseString);
    }



}