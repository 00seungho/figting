package aikopo.ac.kr.fighting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FightingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FightingApplication.class, args);
    }

}
