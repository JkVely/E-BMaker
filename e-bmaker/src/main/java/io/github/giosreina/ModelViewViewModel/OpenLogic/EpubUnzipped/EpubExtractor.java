package io.github.giosreina.ModelViewViewModel.OpenLogic.EpubUnzipped;

import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.zip.*;

public class EpubExtractor {
    public static void ReadZip(ZipFile file) {
        try {
            ZipFile zipFile = file;
            List<String> fileNames = new ArrayList<>();
            for (java.util.Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = e.nextElement();
                if (!entry.isDirectory()) {
                    fileNames.add(entry.getName());
                }
            }
            zipFile.close();
            for(String fileName : fileNames) {
                System.out.println("Archivo: " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
