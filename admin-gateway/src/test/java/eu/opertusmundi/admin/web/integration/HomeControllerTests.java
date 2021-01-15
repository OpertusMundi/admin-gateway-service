package eu.opertusmundi.admin.web.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("testing")
@AutoConfigureMockMvc
class HomeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    // Verifying HTTP Request Matching


    @Test
    void whenValidRootUrlAndMethodAndContentType_thenReturns302() throws Exception {
        this.mockMvc.perform(get("/")
            .contentType("text/html")
        )
        	.andExpect(status().is3xxRedirection())
        	.andExpect(view().name("redirect:/helpdesk/")
        );
    }

}