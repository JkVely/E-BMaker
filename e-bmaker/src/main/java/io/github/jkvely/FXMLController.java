package io.github.jkvely;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import io.github.jkvely.util.MarkdownToHtmlConverter;

public class FXMLController implements Initializable {
    
    @FXML
    private TextArea editorTextArea;
    
    @FXML
    private void handleUploadText(ActionEvent event) {
        String texto = editorTextArea.getText();
        String html = MarkdownToHtmlConverter.convert(texto);
        System.out.println("HTML generado: " + html);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}
