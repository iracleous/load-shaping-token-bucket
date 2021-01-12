package gr.codehub.throttling;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.codehub.throttling.model.ResponseGeometricalData;
import gr.codehub.throttling.model.RequestGeometricalData;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


public class consumer {
    public static void main(String[] args) throws JsonProcessingException {


        final String uri = "http://localhost:8080/geometry";
        RestTemplate restTemplate = new RestTemplate();


        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-api-key", "PX001-1234567890");

        for (int i=1;i<=101;i++) {
            RequestGeometricalData requestGeometricalData = new RequestGeometricalData(1, 1);
            HttpEntity<RequestGeometricalData> entity = new HttpEntity<>(requestGeometricalData, headers);
            ResponseEntity<ResponseGeometricalData> response = restTemplate.postForEntity(uri, entity, ResponseGeometricalData.class);


            System.out.println(i +" -> " +response.getHeaders().get("X-Rate-Limit-Remaining"));
            System.out.println(i +" -> " +response.getHeaders().get("X-Rate-Limit-Retry-After-Seconds"));
            System.out.println(i +" -> " +response.getBody().getDescription());
        }


    }
}
