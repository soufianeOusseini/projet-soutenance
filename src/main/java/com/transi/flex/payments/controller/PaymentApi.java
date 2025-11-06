package com.transi.flex.payments.controller;


import com.transi.flex.payments.Dtos.*;
import com.transi.flex.payments.services.paygate.PaygateService;
import com.transi.flex.reservation.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("payment")
@AllArgsConstructor
public class PaymentApi {

    PaygateService paygateService;
    private ReservationService reservationService;

    @PostMapping(path = "/deposit")
    public DepositResponseDto payment(@RequestBody ClientRequestDto entity){
        return this.paygateService.depotTransactionPaygate(entity);
    }

    @PostMapping(path = "/check-status")
    public CheckResponseDto checkStatus(@RequestBody CheckTransactionDto entity){
        return this.paygateService.checkTransactioStatus(entity);
    }

    //######################## CALLBACK ####################################
    @PostMapping("/confirmation")
    public Object handlePaymentConfirmation(@RequestBody PaygateCallbackDto paymentData) {
        try {
            System.out.println("=================************CALLBACK************============");
            System.out.println("tx_reference"+ paymentData.getTx_reference());
            //Reservation reservation= reservationService.saveAfterPaid(paymentData.getTx_reference());
            return  paymentData;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du traitement du paiement.");
        }
    }

}
