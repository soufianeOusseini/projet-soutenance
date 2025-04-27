// EmailService
package com.transi.flex.mailing.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring6.ISpringTemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import com.transi.flex.mailing.dto.EmailRequest;
import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final CompanyRepository companyRepository;

    private ISpringTemplateEngine templateEngine;

    @Value("${app-url}")
    private String appUrl;

    private static final String TEMPLATE_FOLDER = "templates/emails/";
    private static final String DEFAULT_LOGO_PATH = "static/images/transi-flex-logo.png";

    @SneakyThrows
    public void send(Context context, String template, EmailRequest emailRequest) {
        context.setVariable("appUrl", appUrl);

        if (!context.containsVariable("hasLogo")) {
            prepareCompanyContext(context);
        }

        CompletableFuture.runAsync(() -> {
            Thread t = Thread.currentThread();
            ClassLoader orig = t.getContextClassLoader();
            t.setContextClassLoader(InternetAddress.class.getClassLoader());
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(new InternetAddress(emailRequest.getFrom(), emailRequest.getSenderName()));
                helper.setTo(emailRequest.getTo());
                helper.setSubject(emailRequest.getSubject());
                String htmlContent = parseTemplateToHTML(template + "_" + emailRequest.getLang(), context);
                helper.setText(htmlContent, true);
                addAttachments(helper, emailRequest.getFiles());
                javaMailSender.send(message);
                log.info("email sent to {}", Arrays.asList(emailRequest.getTo()));
            } catch (Exception e) {
                log.error("fail to send email to {}", Arrays.asList(emailRequest.getTo()), e);
            } finally {
                t.setContextClassLoader(orig);
            }
        });
    }

    private void prepareCompanyContext(Context context) {
        try {
            Company currentCompany = null;
            Long companyId = CompanyContextHolder.getCurrentId();

            if (companyId != null) {
                currentCompany = companyRepository.findById(companyId).orElse(null);
            }

            if (currentCompany != null) {
                context.setVariable("companyName", currentCompany.getName());
                context.setVariable("companyEmail", currentCompany.getEmail());
                context.setVariable("companyPhone", currentCompany.getTelephone());
                context.setVariable("companyAddress", currentCompany.getAddress());
                setDefaultLogo(context);
            } else {
                context.setVariable("companyName", "Transi-Flex");
                setDefaultLogo(context);
            }
        } catch (Exception e) {
            log.error("Erreur lors de la préparation du contexte de l'entreprise: {}", e.getMessage(), e);
            context.setVariable("companyName", "Transi-Flex");
            setDefaultLogo(context);
        }
    }

    private void setDefaultLogo(Context context) {
        try {
            ClassPathResource resource = new ClassPathResource(DEFAULT_LOGO_PATH);
            byte[] fileContent = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(fileContent);

            context.setVariable("logoBase64", base64);
            context.setVariable("hasLogo", true);
        } catch (Exception e) {
            log.warn("Impossible de charger le logo par défaut: {}", e.getMessage());
            context.setVariable("hasLogo", false);
        }
    }

    private String parseTemplateToHTML(String template, Context context) {
        return getTemplateEngine().process(template, context);
    }

    private ISpringTemplateEngine getTemplateEngine() {
        if (templateEngine != null) {
            return templateEngine;
        }

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setPrefix(TEMPLATE_FOLDER);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        templateResolver.setOrder(1);
        templateResolver.setCheckExistence(true);

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.addDialect(new Java8TimeDialect());
        engine.setTemplateResolver(templateResolver);

        templateEngine = engine;
        return templateEngine;
    }

    @SneakyThrows
    private void addAttachments(MimeMessageHelper helper, File[] files) {
        if (files == null) {
            return;
        }
        for (File file : files) {
            helper.addAttachment(file.getName(), file);
        }
    }
}