package com.gmail.voronovskyi.yaroslav.dto.mapper;

import com.gmail.voronovskyi.yaroslav.config.MapperCustomConfig;
import com.gmail.voronovskyi.yaroslav.dto.response.PaymentResponse;
import com.gmail.voronovskyi.yaroslav.model.Payment;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(config = MapperCustomConfig.class)
public interface PaymentMapper {

    PaymentResponse paymentToPaymentResponse(Payment payment);

    default Page<PaymentResponse> paymentPageToPaymentResponsePage(Page<Payment> payments) {
        return payments.map(this::paymentToPaymentResponse);
    }
}
