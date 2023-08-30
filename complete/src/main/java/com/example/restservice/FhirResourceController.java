package com.example.restservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FhirResourceController {

    private final FhirResourceCreate fhirResourceCreate;

    @Autowired
    public FhirResourceController(FhirResourceCreate fhirResourceCreate) {
        this.fhirResourceCreate = fhirResourceCreate;
    }

    @GetMapping("/create-fhir-resource")
    public String createFhirResource() {
        try {
            fhirResourceCreate.fhirResourceCreate();
            return "FHIR resource created successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error creating FHIR resource.";
        }
    }
}
