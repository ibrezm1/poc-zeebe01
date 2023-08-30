package com.example.restservice;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.camunda.zeebe.client.ZeebeClient;

@RestController
public class GreetingController {
	
	@Value( "${test.message}" )
	private String testMessage;

	@Autowired
	private ZeebeClient client;


	private static final String template = "Hello, %s! ";
	private final AtomicLong counter = new AtomicLong();

	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template + testMessage, name));
	
	}

  @GetMapping("/start")
  public void startProcessInstance() {
	System.out.println("Started process instance with key: start ");
    client
        .newCreateInstanceCommand()
        .bpmnProcessId("startp")
        .latestVersion()
        .send()
		.join();
	System.out.println("Started process instance with key:end ");
  }

}
