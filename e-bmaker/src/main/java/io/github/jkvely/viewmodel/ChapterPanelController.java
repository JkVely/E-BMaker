package io.github.jkvely.viewmodel;

import io.github.jkvely.model.Classes.EpubChapter;
import io.github.jkvely.util.MarkdownToHtmlConverter;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;

public class ChapterPanelController {
    @FXML private TextField chapterTitleField;
    @FXML private TextArea editorArea;
    @FXML private WebView previewWebView;

    private EpubChapter chapter;

    @FXML
    public void initialize() {
        editorArea.textProperty().addListener((obs, oldText, newText) -> {
            String html = MarkdownToHtmlConverter.convert(newText);
            previewWebView.getEngine().loadContent(html, "text/html");
        });
    }

    public void setChapter(EpubChapter chapter) {
        this.chapter = chapter;
        if (chapter != null) {
            chapterTitleField.setText(chapter.getTitle());
            editorArea.setText(chapter.getContent());
            // Preview inicial
            String html = MarkdownToHtmlConverter.convert(chapter.getContent());
            previewWebView.getEngine().loadContent(html, "text/html");
        }
        chapterTitleField.textProperty().addListener((obs, old, val) -> {
            if (this.chapter != null) this.chapter.setTitle(val);
        });
        editorArea.textProperty().addListener((obs, old, val) -> {
            if (this.chapter != null) this.chapter.setContent(val);
            String html = MarkdownToHtmlConverter.convert(val);
            previewWebView.getEngine().loadContent(html, "text/html");
        });
    }
}
