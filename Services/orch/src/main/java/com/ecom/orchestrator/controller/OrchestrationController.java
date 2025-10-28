//package com.ecom.orchestrator.controller;
//
//import com.ecom.orchestrator.dto.OrchestrationRegistrationDto;
//import com.ecom.orchestrator.service.OrchestrationExecutorService;
//import com.ecom.orchestrator.service.OrchestrationRegistryService;
//import com.ecom.orchestrator.service.UndoService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/orchestration")
//@RequiredArgsConstructor
//@Slf4j
//@Tag(name = "Orchestration", description = "Orchestration management APIs")
//public class OrchestrationController {
//
//    private final OrchestrationRegistryService registryService;
//    private final OrchestrationExecutorService executorService;
//    private final UndoService undoService;
//
//    @PostMapping("/register")
//    @Operation(summary = "Register orchestration definition",
//               description = "Register an orchestration as initiator or worker")
//    public ResponseEntity<Map<String, String>> registerOrchestration(
//            @RequestBody OrchestrationRegistrationDto registration,
//            @RequestHeader(value = "X-Service-Name", required = false) String serviceName) {
//
//        try {
//            if (serviceName == null) {
//                serviceName = registration.getOrchestrationName() + "-service";
//            }
//
//            registryService.registerOrchestration(registration, serviceName);
//
//            return ResponseEntity.ok(Map.of(
//                "status", "SUCCESS",
//                "message", "Registration initiated for orchestration: " + registration.getOrchestrationName()
//            ));
//        } catch (Exception e) {
//            log.error("Error registering orchestration", e);
//            return ResponseEntity.badRequest().body(Map.of(
//                "status", "ERROR",
//                "message", e.getMessage()
//            ));
//        }
//    }
//
//    @PostMapping("/execute/{orchName}")
//    @Operation(summary = "Start orchestration execution",
//               description = "Start execution of a registered orchestration")
//    public ResponseEntity<Map<String, String>> executeOrchestration(
//            @PathVariable String orchName,
//            @RequestBody(required = false) byte[] payload) {
//
//        try {
//            if (payload == null) {
//                payload = new byte[0];
//            }
//
//         //   String flowId = executorService.startOrchestration(orchName, payload);
//
//            return ResponseEntity.ok(Map.of(
//                "status", "SUCCESS",
//                "flowId", flowId,
//                "message", "Orchestration execution started"
//            ));
//        } catch (Exception e) {
//            log.error("Error executing orchestration: {}", orchName, e);
//            return ResponseEntity.badRequest().body(Map.of(
//                "status", "ERROR",
//                "message", e.getMessage()
//            ));
//        }
//    }
//
//    @PostMapping("/undo/{flowId}")
//    @Operation(summary = "Manually trigger undo",
//               description = "Manually trigger undo for a specific orchestration run")
//    public ResponseEntity<Map<String, String>> undoOrchestration(@PathVariable String flowId) {
//
//        try {
//            undoService.manualUndo(flowId);
//
//            return ResponseEntity.ok(Map.of(
//                "status", "SUCCESS",
//                "message", "Undo initiated for flowId: " + flowId
//            ));
//        } catch (Exception e) {
//            log.error("Error triggering undo for flowId: {}", flowId, e);
//            return ResponseEntity.badRequest().body(Map.of(
//                "status", "ERROR",
//                "message", e.getMessage()
//            ));
//        }
//    }
//
//    @GetMapping("/health")
//    @Operation(summary = "Health check", description = "Service health check endpoint")
//    public ResponseEntity<Map<String, String>> health() {
//        return ResponseEntity.ok(Map.of("status", "UP"));
//    }
//}
