package tw.elliot.storm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import tw.elliot.storm.topology.SpringTopology;
import tw.elliot.storm.util.ApplicationContextHolder;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);

        SpringTopology topology = ApplicationContextHolder.getBean(SpringTopology.class);

        //add env check
        topology.submitLocal();
    }
}
