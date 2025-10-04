package com.transi.flex.pdf;

import com.transi.flex.ticket.model.Ticket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@AllArgsConstructor
public class PdfTicketService {

    private final TemplateEngine templateEngine;

    public byte[] generateTicketPdf(Ticket ticket) {
        try {
            // Préparer le contexte Thymeleaf
            Context context = new Context(Locale.FRENCH);
            context.setVariable("ticket", ticket);
            context.setVariable("dateFormatter", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            context.setVariable("timeFormatter", DateTimeFormatter.ofPattern("HH:mm"));

            // Générer le HTML depuis le template
            String html = templateEngine.process("ticket/receipt", context);

            // Convertir HTML en PDF
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(html);
                renderer.layout();
                renderer.createPDF(outputStream);

                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }
}