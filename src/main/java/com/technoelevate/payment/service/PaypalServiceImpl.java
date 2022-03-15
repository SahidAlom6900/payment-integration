package com.technoelevate.payment.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.technoelevate.payment.dto.OrderDto;
import com.technoelevate.payment.exception.PaymentException;
import com.technoelevate.payment.pojo.Order;
import com.technoelevate.payment.repository.PaymentRepository;

@Service
public class PaypalServiceImpl implements PaypalService {

	@Autowired
	private APIContext apiContext;

	@Autowired
	private PaymentRepository paymentRepository;

	private List<OrderDto> orderDtos = new ArrayList<>();

	@Override
	public Payment createPayment(OrderDto orderDto, String cancelUrl, String successUrl) {
		try {
			Amount amount = new Amount();
			amount.setCurrency(orderDto.getCurrency());
			double total = BigDecimal.valueOf(orderDto.getPrice()).setScale(2, RoundingMode.HALF_UP).doubleValue();
			amount.setTotal(String.format("%.2f", total));

			Transaction transaction = new Transaction();
			transaction.setDescription(orderDto.getDescription());
			transaction.setAmount(amount);

			List<Transaction> transactions = new ArrayList<>();
			transactions.add(transaction);

			Payer payer = new Payer();
			payer.setPaymentMethod(orderDto.getMethod());

			Payment payment = new Payment();
			payment.setIntent(orderDto.getIntent());
			payment.setPayer(payer);
			payment.setTransactions(transactions);
			RedirectUrls redirectUrls = new RedirectUrls();
			redirectUrls.setCancelUrl(cancelUrl);
			redirectUrls.setReturnUrl(successUrl);
			payment.setRedirectUrls(redirectUrls);
			System.out.println(payment);
			orderDto.setRequestId(apiContext.getRequestId());
			orderDtos.add(orderDto);
			return payment.create(apiContext);
		} catch (PayPalRESTException exception) {
			throw new PaymentException(exception.getMessage());
		}
	}

	@Override
	public Payment executePayment(String paymentId, String payerId) {
		try {
			Payment payment = new Payment();
			payment.setId(paymentId);
			PaymentExecution paymentExecute = new PaymentExecution();
			paymentExecute.setPayerId(payerId);
			String requestId = apiContext.getRequestId();
			orderDtos.stream().filter(orderDto0 -> orderDto0.getRequestId().equalsIgnoreCase(requestId)).findFirst()
					.ifPresent(orderDto -> {
						Order order = new Order();
						orderDto.setPayerId(payerId);
						orderDto.setPaymentId(paymentId);
						BeanUtils.copyProperties(orderDto, order);
						paymentRepository.save(order);
						orderDtos.remove(orderDto);
					});
			return payment.execute(apiContext, paymentExecute);
		} catch (PayPalRESTException exception) {
			throw new PaymentException(exception.getMessage());
		}
	}

}
