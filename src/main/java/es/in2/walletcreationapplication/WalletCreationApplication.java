package es.in2.walletcreationapplication;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class WalletCreationApplication {

    private static final ObjectMapper OBJECT_MAPPER =
            // sort alphabetically, to ensure same order when hashing.
            JsonMapper.builder().configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true).build();

    public static void main(String[] args) {
        SpringApplication.run(WalletCreationApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        // the ObjectMapper should only be created once per JVM for resource reasons. To ensure thread-safety, get
        // the thread-safe reader and writer instances from it.
        return OBJECT_MAPPER;
    }

//    @Bean
//    ApplicationListener<ApplicationStartedEvent> doOnStart(ObservationRegistry registry) {
//        return event -> generateRecord(registry);
//    }
//
//    public void generateRecord(ObservationRegistry registry) {
//        String something = Observation
//                // Create an instance of Observation and bind it to an ObservationRegistry as stated
//                // in the Micrometer documentation.
//                .createNotStarted("server.job", registry)
//                // To better observe our invocation, add low cardinality keys. These are keys whose
//                // value will have a bounded number of possible values. For high cardinality keys -
//                // having unbounded possible values - use the .highCardinalityKeyValue() method.
//                .lowCardinalityKeyValue("jobType", "string")
//                // Rather than manually calling .start() and .stop(), use the observe(Runnable) method
//                // to isolate the monitored code in its own Runnable closure. You can also use the
//                // observeChecked(CheckedRunnable) for methods that throw Exceptions.
//                .observe(() -> {
//                    log.info("Generating a Record...");
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        log.error("ERROR: {}", e.getMessage());
//                        Thread.currentThread().interrupt();
//                        return "NOTHING";
//                    }
//                    return "SOMETHING";
//                });
//
//        log.info("Result was: " + something);
//    }
//
//    @Bean
//    ObservationTextPublisher observationTextPublisher() {
//        return new ObservationTextPublisher();
//    }

}
