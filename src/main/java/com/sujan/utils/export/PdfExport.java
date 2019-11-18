package com.sujan.utils.export;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


public class PdfExport {

    public boolean exportPDF(Node node, String pdfName) {
        File pdfFile = new File(pdfName);

        WritableImage nodeshot = node.snapshot(new SnapshotParameters(), null);
        File file = new File("memberPDF.png");

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(nodeshot, null), "png", file);
        } catch (IOException ignored) {
        }

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        PDImageXObject pdimage;
        PDPageContentStream content;
        try {
            pdimage = PDImageXObject.createFromFile("memberPDF.png", doc);
            content = new PDPageContentStream(doc, page);
            content.drawImage(pdimage, 0, 0);
            content.close();
            doc.addPage(page);

            //Creating the PDDocumentInformation object
            PDDocumentInformation pdd = doc.getDocumentInformation();

            //Setting the author of the document
            pdd.setAuthor("Sujan Maharjan");

            //Setting the creator of the document
            pdd.setCreator("CMMS");

            //Setting the subject of the document
            pdd.setSubject("Member Details");

            doc.save(pdfFile);
            doc.close();
            file.delete();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
