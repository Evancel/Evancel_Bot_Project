package pro.sky.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.sky.CryptoTool;

@Configuration
public class RestServiceConfiguration {
    @Value("${salt}")
    private String salt;

    @Bean
    public CryptoTool generatedCryptoTool(){
        return new CryptoTool(salt);
    }
}
