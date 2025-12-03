//package com.ecom.orchestrator.integration;
//
//import com.ecom.orchestrator.dto.OrchestrationRegistrationDto;
//import com.ecom.orchestrator.dto.StepDefinitionDto;
//import com.ecom.orchestrator.entity.OrchestrationStatusEnum;
//import com.ecom.orchestrator.entity.OrchestrationTemplate;
//import com.ecom.orchestrator.repository.OrchestrationTemplateRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.KafkaContainer;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Testcontainers
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Disabled
//class OrchestrationIntegrationTest {
//
//    @Container
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
//            .withDatabaseName("orchestrator_test")
//            .withUsername("test")
//            .withPassword("test");
//
//    @Container
//    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.username", postgres::getUsername);
//        registry.add("spring.datasource.password", postgres::getPassword);
//        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
//    }
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private OrchestrationTemplateRepository orchestrationTemplateRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void testFullOrchestrationFlow() throws Exception {
//        // Test initiator registration
//        OrchestrationRegistrationDto initiatorRegistration = new OrchestrationRegistrationDto();
//       // initiatorRegistration.setOrchName("testOrchestration");
//        initiatorRegistration.setAs("initiator");
//        initiatorRegistration.setType("sequential");
//        initiatorRegistration.setSteps(List.of(
//                new StepDefinitionDto(1, "step1", "String"),
//                new StepDefinitionDto(2, "step2", "String")
//        ));
//
//        ResponseEntity<Map> response = restTemplate.postForEntity(
//                "http://localhost:" + port + "/api/orchestration/register",
//                initiatorRegistration,
//                Map.class
//        );
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("SUCCESS", response.getBody().get("status"));
//
//        // Verify orchestration was created in database
//        Thread.sleep(1000); // Allow time for async processing
//
//        OrchestrationTemplate template = orchestrationTemplateRepository
//                .findByOrchName("testOrchestration")
//                .orElse(null);
//
//        assertNotNull(template);
//        assertEquals("testOrchestration", template.getOrchName());
//        assertEquals(OrchestrationStatusEnum.PENDING, template.getStatus()); // No workers registered yet
//
//        // Test worker registration
//        OrchestrationRegistrationDto workerRegistration = new OrchestrationRegistrationDto();
//        workerRegistration.setOrchName("testOrchestration");
//        workerRegistration.setAs("worker");
//        workerRegistration.setSteps(List.of(
//                new StepDefinitionDto(null, "step1", "String")
//        ));
//
//        ResponseEntity<Map> workerResponse = restTemplate.postForEntity(
//                "http://localhost:" + port + "/api/orchestration/register",
//                workerRegistration,
//                Map.class
//        );
//
//        assertEquals(HttpStatus.OK, workerResponse.getStatusCode());
//        assertEquals("SUCCESS", workerResponse.getBody().get("status"));
//
//        // Test orchestration execution
//        ResponseEntity<Map> executeResponse = restTemplate.postForEntity(
//                "http://localhost:" + port + "/api/orchestration/execute/testOrchestration",
//                "test payload".getBytes(),
//                Map.class
//        );
//
//        assertEquals(HttpStatus.OK, executeResponse.getStatusCode());
//        assertEquals("SUCCESS", executeResponse.getBody().get("status"));
//        assertNotNull(executeResponse.getBody().get("flowId"));
//    }
//
//    @Test
//    void testHealthEndpoint() {
//        ResponseEntity<Map> response = restTemplate.getForEntity(
//                "http://localhost:" + port + "/api/orchestration/health",
//                Map.class
//        );
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("UP", response.getBody().get("status"));
//    }
//
//    @Test
//    void testRegisterOrchestration_InvalidRole() {
//        OrchestrationRegistrationDto invalidRegistration = new OrchestrationRegistrationDto();
//        invalidRegistration.setOrchName("invalidOrch");
//        invalidRegistration.setAs("invalid_role");
//        invalidRegistration.setType("sequential");
//        invalidRegistration.setSteps(List.of());
//
//        ResponseEntity<Map> response = restTemplate.postForEntity(
//                "http://localhost:" + port + "/api/orchestration/register",
//                invalidRegistration,
//                Map.class
//        );
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("ERROR", response.getBody().get("status"));
//    }
//}
