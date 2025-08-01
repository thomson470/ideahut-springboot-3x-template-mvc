package net.ideahut.springboot.template.controller;



import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.init.InitRequest;

@Public
@RestController
@RequestMapping(
    path = "/warmup",
    consumes = APPLICATION_JSON_VALUE
)
class WarmUpController {

    @PostMapping
    ResponseEntity<String> post(@RequestBody @Valid InitRequest initRequest) {
        return ResponseEntity.ok(UUID.randomUUID().toString() + initRequest);
    }
}