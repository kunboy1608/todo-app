package com.hoangdp.todo.resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.hoangdp.todo.resource.admin.AdminResource;

public class AdminResourceTest {
    @WithMockUser(authorities = "ADMIN")
    @Test
    void endpointWhenAdminAuthorityThenAuthorized() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new AdminResource()).build();
        mvc.perform(get("/api/v1/admin/hello"))
                .andExpect(status().isOk());
    }
}
