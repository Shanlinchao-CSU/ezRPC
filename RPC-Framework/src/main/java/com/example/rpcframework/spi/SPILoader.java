package com.example.rpcframework.spi;

public class SPILoader {
    private String SYS_SPI_DIR = "META-INF/system";
    private String PERSONAL_SPI_DIR = "META-INF/personal";
    private String[] prefix = {SYS_SPI_DIR, PERSONAL_SPI_DIR};
    public void load(Class<?> aclass) {

    }
}
