package com.github.elazarl.rangetree;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Class that compiles and shows a graph in the dotfile format.
 * Works in Linux only (assumes dot and google-chrome in path).
 */
public class Graphviz {
    static void show(String dotfile) {
        try {
            File tempFile = File.createTempFile("rangetree", "dot");
            Files.write(dotfile.getBytes(), tempFile);
            String[] cmdarray = {
                    "dot", tempFile.getAbsolutePath(), "-Tsvg", "-o", "out.svg"
            };
            System.out.println(Arrays.toString(cmdarray));
            Runtime.getRuntime().exec(cmdarray);
            Runtime.getRuntime().exec(new String[] {
                "google-chrome", "out.svg"
            }).waitFor();
            new File("out.svg").deleteOnExit();
            tempFile.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
