package co.edu.icesi.dev.outcome_curr_mgmt.testing.system.rs.faculty;

import co.edu.icesi.dev.outcome_curr.mgmt.model.stdindto.curriculum_qa.PerfLvlInDTO;
import co.edu.icesi.dev.outcome_curr.mgmt.model.stdindto.faculty.AcadProgramInDTO;
import co.edu.icesi.dev.outcome_curr.mgmt.model.stdindto.faculty.FacultyInDTO;
import co.edu.icesi.dev.outcome_curr.mgmt.model.stdoutdto.curriculum_qa.PerfLvlOutDTO;
import co.edu.icesi.dev.outcome_curr.mgmt.model.stdoutdto.faculty.AcadProgramOutDTO;
import co.edu.icesi.dev.outcome_curr.mgmt.model.stdoutdto.faculty.FacultyOutDTO;
import co.edu.icesi.dev.outcome_curr.mgmt.rs.faculty.AuthPerfLvlController;
import co.edu.icesi.dev.outcome_curr_mgmt.testing.system.rs.util.BaseSmokeIT;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {AuthPerfLvlController.class})
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Timeout(15)
public class PerfLvlControllerSmokeIT extends BaseSmokeIT {

    public static final String OUT_CURR_TEST_USER = "OutCurrTestUser";

    public static final String USER_PASSWORD = "123456";

    private static String testUserJWTToken;

    private static PerfLvlInDTO perfLvlInDTO;
    private static PerfLvlOutDTO perfLvlOutDTO;


    public static final String OUTCURRAPI_V_1_AUTH_PERFORMANCE_LEVELS = "/outcurrapi/v1/auth/faculties/";



    private static final String PERF_LVL_NAME_ENG = "Excellent Performance Level";
    private static final String PERF_LVL_NAME_SPA = "Nivel de desempeño excelente";



    @Value("${test.server.url}")
    private String server;

    @BeforeAll
    void init() {
        testUserJWTToken = getTestUserJWTToken(OUT_CURR_TEST_USER, USER_PASSWORD, server);

        perfLvlInDTO = PerfLvlInDTO.builder()
                .plNameEng(PERF_LVL_NAME_ENG)
                .plNameSpa(PERF_LVL_NAME_SPA)
                .build();


    }

    @AfterAll
    void cleanUp() {
        deletePerformanceLevel(1L, 1L, perfLvlOutDTO.plId());
    }





    @Test
    void testCreatePerformanceLevelHappyPath() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        String token = "Bearer " + testUserJWTToken;
        HttpHeaders headers = getHeaders();
        headers.set("Authorization", token);

        HttpEntity<PerfLvlInDTO> jwtEntity = new HttpEntity<>(perfLvlInDTO, headers);

        long facultyId = 1L;
        long acadProgId = 1L;

        ResponseEntity<PerfLvlOutDTO> response = testRestTemplate.exchange(
                server + OUTCURRAPI_V_1_AUTH_PERFORMANCE_LEVELS + facultyId + "/acad_programs/" + acadProgId
                        + "/performance_levels",
                HttpMethod.POST,
                jwtEntity,
                PerfLvlOutDTO.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(PERF_LVL_NAME_ENG, response.getBody().plNameEng());
        assertEquals(PERF_LVL_NAME_SPA, response.getBody().plNameSpa());
        perfLvlOutDTO = response.getBody();
    }

    @Test
    void testGetAllPerformanceLevelsHappyPath() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        String token = "Bearer " + testUserJWTToken;
        HttpHeaders headers = getHeaders();
        headers.set("Authorization", token);

        HttpEntity<PerfLvlInDTO> jwtEntity = new HttpEntity<>(headers);

        long facultyId = 1L;
        long acadProgId = 1L;

        ResponseEntity<String> response = testRestTemplate.exchange(
                server + OUTCURRAPI_V_1_AUTH_PERFORMANCE_LEVELS + facultyId + "/acad_programs/" + acadProgId
                        + "/performance_levels",
                HttpMethod.GET,
                jwtEntity,
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetPerformanceLevelWhenPerformanceLevelDoesNotExist() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        String token = "Bearer " + testUserJWTToken;
        HttpHeaders headers = getHeaders();
        headers.set("Authorization", token);

        HttpEntity<PerfLvlInDTO> jwtEntity = new HttpEntity<>(headers);

        long facultyId = 1L;
        long acadProgId = 1L;
        long perfLvlId = 999999999L;

        ResponseEntity<String> response = testRestTemplate.exchange(
                server + OUTCURRAPI_V_1_AUTH_PERFORMANCE_LEVELS + facultyId + "/acad_programs/" + acadProgId
                        + "/performance_levels/" + perfLvlId,
                HttpMethod.GET,
                jwtEntity,
                String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetPerformanceLevelHappyPath() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        String token = "Bearer " + testUserJWTToken;
        HttpHeaders headers = getHeaders();
        headers.set("Authorization", token);

        HttpEntity<PerfLvlInDTO> jwtEntity = new HttpEntity<>(headers);

        long facultyId = 1L;
        long acadProgId = 1L;
        long perfLvlId = perfLvlOutDTO.plId();

        ResponseEntity<String> response = testRestTemplate.exchange(
                server + OUTCURRAPI_V_1_AUTH_PERFORMANCE_LEVELS + facultyId + "/acad_programs/" + acadProgId
                        + "/performance_levels/" + perfLvlId,
                HttpMethod.GET,
                jwtEntity,
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    private void deletePerformanceLevel(long facultyId, long acadProgId, long perfLvlId) {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        System.out.println("Test User JWT Token: " + testUserJWTToken);
        String token = "Bearer " + testUserJWTToken;
        HttpHeaders headers = getHeaders();
        headers.set("Authorization", token);

        HttpEntity<PerfLvlInDTO> jwtEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                server + OUTCURRAPI_V_1_AUTH_PERFORMANCE_LEVELS + facultyId + "/acad_programs/" + acadProgId
                        + "/performance_levels/" + perfLvlId,
                HttpMethod.DELETE,
                jwtEntity,
                String.class
        );
    }
}
