package com.transi.flex.payments.services.paygate;


import com.transi.flex.payments.Dtos.CheckResponseDto;
import com.transi.flex.payments.Dtos.CheckTransactionDto;
import com.transi.flex.payments.Dtos.ClientRequestDto;
import com.transi.flex.payments.Dtos.DepositResponseDto;

public interface PaygateService {
    Object depotTransaction(Object data);
    DepositResponseDto depotTransactionPaygate(ClientRequestDto data);
    CheckResponseDto checkTransactioStatus(CheckTransactionDto data);
}
