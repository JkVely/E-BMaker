package io.github.giosreina.ModelViewViewModel.OpenLogic.EpubUnzipped;
import java.util.zip.*;

public class PruebaEpub {
    public static void main(String[] args) {
        try {
            // Asegúrate de que el archivo ZIP exista en la ruta especificada
            ZipFile file = new ZipFile("C:\\Users\\ASUS\\Documents\\Gnubies\\Proyecto\\E-BMaker\\e-bmaker\\src\\main\\java\\io\\github\\giosreina\\ModelViewViewModel\\OpenLogic\\EpubUnzipped\\Abstract_Factory.zip");
            System.out.println("El archivo existe: " + file);
            EpubExtractor.ReadZip(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
