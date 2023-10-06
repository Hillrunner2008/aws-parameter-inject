/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dcnorris.aws;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author dcnorris
 */
@Component
public class DataSourceConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceConfiguration.class);

    @AwsParameter("db.password:test")
    private String dbPassword;

    @PostConstruct
    private void init() {
        LOG.info(dbPassword);
    }
}
