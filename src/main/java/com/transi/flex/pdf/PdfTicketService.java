package com.transi.flex.pdf;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.transi.flex.agency.dao.AgencyRepository;
import com.transi.flex.agency.model.Agency;
import com.transi.flex.colis.model.Colis;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.config.AgencyContextHolder;
import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.file.service.FileUtility;
import com.transi.flex.ticket.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PdfTicketService {

    @Value("${upload.path}")
    private String uploadPath;

    private final TemplateEngine templateEngine;

    private final CompanyRepository companyRepository;

    private final AgencyRepository agencyRepository;

    private final FileUtility fileUtility;

    public byte[] generateTicketPdf(Ticket ticket) {
        try {
            Company company = null;
            Agency agency = null;
            if(CompanyContextHolder.getCurrentId() !=null){
                company = companyRepository.findById(CompanyContextHolder.getCurrentId()).orElse(null);
            }
            if(AgencyContextHolder.getCurrentAgencyId() !=null){
                agency = agencyRepository.findById(AgencyContextHolder.getCurrentAgencyId()).orElse(null);
            }
            // 🔹 Générer le QR code
            String qrText = "Ticket N°: " + ticket.getNumero() + "\n" +
                    "Nom: " + (ticket.getUser() != null ? ticket.getUser().getLastName() : ticket.getClientNom()) + "\n" +
                    "Trajet: " + (ticket.getTrajet() != null ? ticket.getTrajet().getNom() : "N/A") + "\n" +
                    "Montant: " + ticket.getPrix() + " F CFA";
            String qrBase64 = generateQrCodeBase64(qrText);
            // tu peux ensuite stocker ça dans le contexte
            Context context = new Context(Locale.FRENCH);
            context.setVariable("ticket", ticket);
            context.setVariable("qrCodeBase64", qrBase64);
            context.setVariable("logo", fileUtility.getPhoto(company !=null ? company.getLogoPath() : Objects.requireNonNull(agency).getCompany().getLogoPath(), agency.getCompany()));
            context.setVariable("formattedDate",
                    ticket.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            context.setVariable("dateFormatter", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            context.setVariable("timeFormatter", DateTimeFormatter.ofPattern("HH:mm"));

            // 🔹 Générer le HTML depuis le template Thymeleaf
            String html = templateEngine.process("ticket/receipt", context);

            // 🔹 Convertir le HTML en PDF
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

    public byte[] generateColisPdf(Colis colis) {
        try {
            Company company = null;
            Agency agency = null;
            if(CompanyContextHolder.getCurrentId() != null){
                company = companyRepository.findById(CompanyContextHolder.getCurrentId()).orElse(null);
            }
            if(AgencyContextHolder.getCurrentAgencyId() != null){
                agency = agencyRepository.findById(AgencyContextHolder.getCurrentAgencyId()).orElse(null);
            }

            // Générer le QR code
            String qrText = "Colis N°: " + colis.getNumero() + "\n" +
                    "Expéditeur: " + colis.getExpediteur() + "\n" +
                    "Destinataire: " + colis.getDestinateur() + "\n" +
                    "Trajet: " + (colis.getTrajet() != null ? colis.getTrajet().getNom() : "N/A") + "\n" +
                    "Montant: " + colis.getPrix() + " F CFA";
            String qrBase64 = generateQrCodeBase64(qrText);

            Context context = new Context(Locale.FRENCH);
            context.setVariable("colis", colis);
            context.setVariable("qrCodeBase64", qrBase64);
            context.setVariable("logo", fileUtility.getPhoto(
                    company != null ? company.getLogoPath() : Objects.requireNonNull(agency).getCompany().getLogoPath(),
                    agency != null ? agency.getCompany() : company
            ));
            context.setVariable("dateFormatter", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            context.setVariable("timeFormatter", DateTimeFormatter.ofPattern("HH:mm"));

            // Générer le HTML depuis le template Thymeleaf
            String html = templateEngine.process("colis", context);

            // Convertir le HTML en PDF
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(html);
                renderer.layout();
                renderer.createPDF(outputStream);
                return outputStream.toByteArray();
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF du colis", e);
        }
    }


    private String generateQrCodeBase64(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 150, 150);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(qrImage, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        }
    }

}