package com.smhu.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@RequestMapping("/api")
public class TestController {

    @GetMapping("/readFile")
    public ResponseEntity<?> getFile() {
        try {
            readTxtFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public void readTxtFile() throws FileNotFoundException, IOException {
        InputStreamReader isr = new FileReader(new File("WEB-INF\\test.txt"));
        BufferedReader br = new BufferedReader(isr);
        int c;
        while ((c = br.read()) != -1) {
            System.out.println(c);
        }
    }
}
