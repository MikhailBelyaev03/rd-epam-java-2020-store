package com.epam.rd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Тестовое сообщение №1");
        logger.info("Тестовое сообщение №2");
        logger.info("Тестовое сообщение №3");
    }
}
