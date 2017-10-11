package com.test;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        File file = new File("E:/sources/spring-cloud-simples");
        File[] files = file.listFiles();
        for (File f : files) {
            if(f.isFile()||".git".equals(f.getName())||".settings".equals(f.getName())){
                continue;
            }
            System.out.println(String.format("<module>%s</module>",f.getName()));
        }
    }

}
