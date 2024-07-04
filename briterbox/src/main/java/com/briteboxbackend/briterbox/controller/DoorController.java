package com.briteboxbackend.briterbox.controller;//package com.britebox.springbootBackend.controller;
//
//import com.britebox.springbootBackend.repository.DoorService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//;
//
//@RestController
//@RequestMapping("/doors")
//public class DoorController {
//
//    private final DoorService doorService;
//
//    @Autowired
//    public DoorController(DoorService doorService) {
//        this.doorService = doorService;
//    }
//
//    @PostMapping("/open/{doorNumber}")
//    public ResponseEntity<String> openDoor(@PathVariable int doorNumber) {
//        System.out.println("I am in");
//        try {
//            doorService.openDoor(doorNumber);
//            return ResponseEntity.ok("Door control request sent for Door " + doorNumber);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Failed to send door control request for Door " + doorNumber + ". Error: " + e.getMessage());
//        }
//    }
//}
