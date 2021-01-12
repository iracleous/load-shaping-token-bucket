package gr.codehub.throttling.controller;

import gr.codehub.throttling.model.ResponseGeometricalData;
import gr.codehub.throttling.model.RequestGeometricalData;
import gr.codehub.throttling.service.PricingPlanService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculationController {
    @Autowired
    private PricingPlanService pricingPlanService;


    @PostMapping(value = "/geometry")
    public ResponseEntity<ResponseGeometricalData> rectangle(@RequestHeader(value = "X-api-key") String apiKey,
                                                             @RequestBody RequestGeometricalData dimensions) {

        Bucket bucket = pricingPlanService.resolveBucket(apiKey);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
             return ResponseEntity.ok()
                    .header("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()))
                    .body(new ResponseGeometricalData("rectangle", dimensions.getLength() * dimensions.getWidth()));
        }


        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds",
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
    }

}
