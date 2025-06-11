package io.github.giosreina.ModelViewViewModel.OpenLogic.EpubUnzipped;
import java.util.zip.*;
import java.util.*;

public class PruebaEpub {
    public static void main(String[] args) {
        try {
            Map<String, List<String>> htmlAndXhtmlContent = EpubExtractor.FindHtmlAndXhtml("C:\\Users\\ASUS\\Documents\\htmlsDePrueba.zip");

            
            for(List<String> content : htmlAndXhtmlContent.values()) {
                for(String line : content) {
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
