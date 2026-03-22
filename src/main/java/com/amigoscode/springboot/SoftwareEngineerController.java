package com.amigoscode.springboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/software-engineers")
public class SoftwareEngineerController {

    private final SoftwareEngineerService softwareEngineerService;

    public SoftwareEngineerController(SoftwareEngineerService softwareEngineerService) {
        this.softwareEngineerService = softwareEngineerService;
    }

    @GetMapping  //Only VIEWER and ADMIN can see the full list.
    @PreAuthorize("hasAuthority('SCOPE_VIEWER') or hasAuthority('SCOPE_ADMIN')")
    public List<SoftwareEngineer> getEngineers(Authentication authentication){
        if (authentication != null) {
            System.out.println("User Authorities: " + authentication.getAuthorities());
        }
        return softwareEngineerService.getAllSoftwareEngineers();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('SCOPE_EDITOR') or hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<SoftwareEngineer> getEngineerById(@PathVariable("id") Integer id) {
        SoftwareEngineer engineer = softwareEngineerService.getSoftwareEngineersById(id);
        return ResponseEntity.ok(engineer);
    }

    @PostMapping //Only EDITOR and ADMIN can add new engineers.
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_EDITOR')")
    public void addNewSoftwareEngineer(@RequestBody SoftwareEngineer softwareEngineer, Authentication authentication){
        System.out.println("User Authorities: " + authentication.getAuthorities());
        log.info("Making post! with body {}", softwareEngineer);
        softwareEngineerService.insertsoftwareEngineer(softwareEngineer);
    }




}
