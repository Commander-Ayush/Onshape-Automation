package com.onhsape.app.onshapeautomationv1.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Component
public class NodeAutomationStarter implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        ProcessBuilder builder = new ProcessBuilder(
                "node",
                "server.js"
        );

        builder.directory(new File("Puppeteer Automation"));
        builder.redirectErrorStream(true);
        builder.inheritIO();

        Process process = builder.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        new Thread(() -> {
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    System.out.println("[NODE SERVER] " + line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
