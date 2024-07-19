package com.lawrence.corejava.inputandoutput;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileReaderExample {
    private final String path;

    FileReaderExample(final String path) {
        this.path = this.getClass().getClassLoader().getResource(path).getPath();
    }

    public void readInCharacter() {
        try (InputStream is = new FileInputStream(path);
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            char[] ch = new char[1];
            while (reader.read(ch) != -1) {
                System.out.println(ch);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readInLine() {
        try (InputStream is = new FileInputStream(path);
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
