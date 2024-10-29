//package ua.knu.knudev.knudevsecurity.service;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//
//@WebMvcTest(SecuredController.class)
//class AuthenticationServiceTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    public void testAdminAccess() throws Exception {
//        mockMvc.perform(get("/admin"))
//                .andExpect(status().isOk());
//    }
//}