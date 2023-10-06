/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dcnorris.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;

/**
 *
 * @author dcnorris
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class AwsConfiguration {

    @Value("${aws.app.name}")
    private String appName;
    @Value("${aws.app.env}")
    private String appEnv;

    @Lazy(false)
    @Bean
    public AwsParamBeanPostProcessor setupAwsParamBeanPostProcessor(SsmClient ssmClient) {
        AwsParamBeanPostProcessor beanPostProcessor = new AwsParamBeanPostProcessor(ssmClient, appName, appEnv);
        return beanPostProcessor;
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(@Value("${aws.app.name:}") String awsCredentialProfileName) {
        if (awsCredentialProfileName != null && !awsCredentialProfileName.isBlank()) {
            final ProfileCredentialsProvider create = ProfileCredentialsProvider.create(awsCredentialProfileName);
            return AwsCredentialsProviderChain.builder()
                    .addCredentialsProvider(create)
                    .addCredentialsProvider(InstanceProfileCredentialsProvider.create())
                    .build();
        }
        return AwsCredentialsProviderChain.builder()
                .addCredentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();
    }

    @Lazy(false)
    @Bean
    @ConditionalOnBean(value = AwsCredentialsProvider.class)
    public SsmClient ssmClient(AwsCredentialsProvider awsCredentialsProvider) {
        return SsmClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(Region.US_EAST_1)
                .httpClient(UrlConnectionHttpClient.builder().build())
                .build();
    }
}
