package com.example.restservice;



import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.healthcare.v1.CloudHealthcare;
import com.google.api.services.healthcare.v1.CloudHealthcareScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

@Service
public class FhirResourceCreate {
  private static final String FHIR_NAME = "projects/%s/locations/%s/datasets/%s/fhirStores/%s";
  private static final JsonFactory JSON_FACTORY = new GsonFactory();
  private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  public static void fhirResourceCreate()
      throws IOException, URISyntaxException {
     String fhirStoreName =
        String.format(
            FHIR_NAME, "plated-ensign-390102", "us-central1", "tempdataset", "fhrdataset");
     String resourceType = "Patient";

    // Initialize the client, which will be used to interact with the service.
    CloudHealthcare client = createClient();
    HttpClient httpClient = HttpClients.createDefault();
    String uri = String.format("%sv1/%s/fhir/%s", client.getRootUrl(), fhirStoreName, resourceType);
    URIBuilder uriBuilder = new URIBuilder(uri).setParameter("access_token", getAccessToken());
    StringEntity requestEntity =
        new StringEntity("{\"resourceType\": \"" + resourceType + "\", \"language\": \"en\"}");

    HttpUriRequest request =
        RequestBuilder.post()
            .setUri(uriBuilder.build())
            .setEntity(requestEntity)
            .addHeader("Content-Type", "application/fhir+json")
            .addHeader("Accept-Charset", "utf-8")
            .addHeader("Accept", "application/fhir+json; charset=utf-8")
            .build();

    // Execute the request and process the results.
    HttpResponse response = httpClient.execute(request);
    HttpEntity responseEntity = response.getEntity();
    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
      System.err.print(
          String.format(
              "Exception creating FHIR resource: %s\n", response.getStatusLine().toString()));
      responseEntity.writeTo(System.err);
      throw new RuntimeException();
    }
    System.out.print("FHIR resource created: ");
    responseEntity.writeTo(System.out);
  }

  private static CloudHealthcare createClient() throws IOException {
    // Use Application Default Credentials (ADC) to authenticate the requests
    // For more information see https://cloud.google.com/docs/authentication/production
    // plated-ensign-390102
    // projects/plated-ensign-390102/locations/us-central1/datasets/tempdataset
    // 
    String jsonKeyFilePath = "src/main/resources/key.json";
    GoogleCredentials credential = GoogleCredentials.fromStream(
                    new FileInputStream(jsonKeyFilePath))
                    .createScoped(Collections.singleton(CloudHealthcareScopes.CLOUD_PLATFORM));

    // Create a HttpRequestInitializer, which will provide a baseline configuration to all requests.
    HttpRequestInitializer requestInitializer =
        request -> {
          new HttpCredentialsAdapter(credential).initialize(request);
          request.setConnectTimeout(60000); // 1 minute connect timeout
          request.setReadTimeout(60000); // 1 minute read timeout
        };

    // Build the client for interacting with the service.
    return new CloudHealthcare.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
        .setApplicationName("your-application-name")
        .build();
  }

  private static String getAccessToken() throws IOException {
    String jsonKeyFilePath = "src/main/resources/key.json";
    GoogleCredentials credential = GoogleCredentials.fromStream(
                    new FileInputStream(jsonKeyFilePath))
                    .createScoped(Collections.singleton(CloudHealthcareScopes.CLOUD_PLATFORM));
    /* GoogleCredentials credential =
        GoogleCredentials.getApplicationDefault()
            .createScoped(Collections.singleton(CloudHealthcareScopes.CLOUD_PLATFORM));
    */
    return credential.refreshAccessToken().getTokenValue();
  }
}