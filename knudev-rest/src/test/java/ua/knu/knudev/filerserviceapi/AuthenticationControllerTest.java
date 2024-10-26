//package ua.knu.knudev.filerserviceapi;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.RequestBuilder;
//import ua.knu.knudev.fileserviceapi.controller.AuthenticationController;
//
//import static javax.swing.UIManager.get;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(AuthenticationController.class)
//class AuthenticationControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    public void testAdminAccess() throws Exception {
//        mockMvc.perform((RequestBuilder) get("/admin"))
//                .andExpect(status().isOk());
//    }
//}