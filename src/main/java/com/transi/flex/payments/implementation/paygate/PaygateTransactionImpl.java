package com.transi.flex.payments.implementation.paygate;


import com.transi.flex.payments.Dtos.*;
import com.transi.flex.payments.entities.DepositRequestPaygate;
import com.transi.flex.payments.repositories.DepositRequestDao;
import com.transi.flex.payments.services.paygate.PaygateService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Transactional
@Service
public class PaygateTransactionImpl implements PaygateService {
    private WebClient webClient;
    private DepositRequestDao depositRequestDao;

    //    private Environment env;
    private static final String AUTH_TOKEN = "0f4b917b-4923-4bbb-9071-24be4dfedb1f";

    @Override
    public Object depotTransaction(Object data) {

        Mono<Object> result = webClient
                .post()
                .uri("/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(data)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new Exception("Erreur client tmoney")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new Exception("Erreur Serveur tmoney")))
                .bodyToMono(Object.class);

        return result.block();
    }

    @Override
    public DepositResponseDto depotTransactionPaygate(ClientRequestDto clientRequestDto) {
        // Construire la requête
        DepositRequestDto depositRequestDto = createDepositRequest(clientRequestDto);

        // Envoyer la requête et récupérer la réponse
        return sendDepositRequest(depositRequestDto);
    }

    // Méthode pour construire DepositRequestDto
    private DepositRequestDto createDepositRequest(ClientRequestDto clientRequestDto) {
        return DepositRequestDto.builder()
                .amount(clientRequestDto.getAmount())
                .auth_token(AUTH_TOKEN)
                .phone_number(clientRequestDto.getPhone())
                .description("test application gestion de bus")
                .identifier(UUID.randomUUID().toString())
                .network(clientRequestDto.getNetwork())
                .build();
    }

    // Méthode pour envoyer la requête
    private DepositResponseDto sendDepositRequest(DepositRequestDto depositRequestDto) {
        return webClient
                .post()
                .uri("/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(depositRequestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new Exception("Erreur client TMoney")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new Exception("Erreur serveur TMoney")))
                .bodyToMono(DepositResponseDto.class)
                .block();
    }

    private DepositRequestPaygate saveInitialDeposit(DepositRequestDto depositRequestDto, DepositResponseDto depositResponseDto) {
        DepositRequestPaygate depositRequestPaygate = DepositRequestPaygate.builder()
                .phone_number(depositRequestDto.getPhone_number())
                .amount(depositRequestDto.getAmount())
                .description(depositRequestDto.getDescription())
                .identifier(depositRequestDto.getIdentifier())
                .network(depositRequestDto.getNetwork())
                .tx_reference(depositResponseDto.getTx_reference())
                .status(2)
                .build();

        System.out.println("phone_number "+ depositRequestPaygate.getPhone_number());
        System.out.println("amount "+ depositRequestPaygate.getAmount());
        System.out.println("description "+ depositRequestPaygate.getDescription());
        System.out.println("identifier "+ depositRequestPaygate.getIdentifier());
        System.out.println("network "+ depositRequestPaygate.getNetwork());
        System.out.println("tx_reference "+ depositRequestPaygate.getTx_reference());
        System.out.println("status "+ depositRequestPaygate.getStatus());

        return this.depositRequestDao.save(depositRequestPaygate);
    }

    // Méthode pour créer CheckTransactionDto
    private CheckTransactionDto createCheckTransaction(DepositResponseDto depositResponseDto) {
        return CheckTransactionDto.builder()
                .tx_reference(depositResponseDto.getTx_reference())
                .auth_token(AUTH_TOKEN)
                .build();
    }

    // Méthode pour vérifier le statut de la transaction
    private CheckResponseDto checkTransactionStatus(CheckTransactionDto checkTransactionDto) {
        return this.checkTransactioStatus(checkTransactionDto);
    }


    @Override
    public CheckResponseDto checkTransactioStatus(CheckTransactionDto data) {

        data.setAuth_token(AUTH_TOKEN);
        Mono<CheckResponseDto> result = webClient
                .post()
                .uri("/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(data)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new Exception("Erreur client tmoney")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new Exception("Erreur Serveur tmoney")))
                .bodyToMono(CheckResponseDto.class);
        CheckResponseDto checkResponseDto = result.block();

        return checkResponseDto;
    }
}
