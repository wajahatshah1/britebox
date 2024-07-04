package com.briteboxbackend.briterbox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.config.annotation.DelegatingWsConfiguration;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

@Configuration
public class WebServiceConfig {
    @Bean
    public static DelegatingWsConfiguration annotationActionEndpointMapping() {
        return new DelegatingWsConfiguration();
    }
    @Bean
    public SaajSoapMessageFactory messageFactory() {
        return new SaajSoapMessageFactory(); // Use default constructor
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate(messageFactory());
        // Configure webServiceTemplate if needed
        return webServiceTemplate;
    }
}
