package io.github.jkvely;

import io.github.jkvely.util.MarkdownToHtmlConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class FXMLController {
    
    @FXML
    private TextArea editorTextArea;
    
    @FXML
    private void handleUploadText(ActionEvent event) {
        String texto = editorTextArea.getText();
        String html = MarkdownToHtmlConverter.convert(texto);
        System.out.println("HTML generado: " + html);
    }
}
