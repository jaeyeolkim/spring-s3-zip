package com.example.springs3.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class S3ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    private static final String FILE_NAME = "profile.jpeg";

    @DisplayName("S3 싱글 파일 다운로드")
    @Test
    public void download() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(
                        get("/download/" + FILE_NAME)
                ).andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String contentType = response.getContentType();
        int contentLength = response.getContentLength();
        String contentDisposition = response.getHeader("Content-Disposition");

        assertThat(contentType).isEqualTo(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        assertThat(contentLength).isGreaterThan(0);
        assertThat(contentDisposition).contains(FILE_NAME);
    }

    @DisplayName("S3 ZIP 파일 다운로드")
    @Test
    public void zipTest() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(
                        get("/download-zip")
                ).andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String contentDisposition = response.getHeader("Content-Disposition");

        assertThat(HttpStatus.resolve(response.getStatus())).isEqualTo(HttpStatus.OK);
        assertThat(contentDisposition).contains(".zip");
    }
}