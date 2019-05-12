package com.mvp.java.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SalesmanServiceTest {

    private SalesmanService salesmanService;

    @BeforeEach
    void init() {
        this.salesmanService = new SalesmanService();
    }

    @Test
    void initTest() throws IOException {
        salesmanService.init();
    }
}