import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kuke.survey", "controller"})
public class SurveyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SurveyApiApplication.class, args);

    }
}
