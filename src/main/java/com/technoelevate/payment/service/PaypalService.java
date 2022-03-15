package com.technoelevate.payment.service;

import com.paypal.api.payments.Payment;
import com.technoelevate.payment.dto.OrderDto;

public interface PaypalService {
	 Payment createPayment(OrderDto orderDto,String cancelUrl,String successUrl);

	 Payment executePayment(String paymentId, String payerId);
}
