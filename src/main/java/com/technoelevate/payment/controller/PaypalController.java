package com.technoelevate.payment.controller;

import static com.technoelevate.payment.common.PaymentConstants.BASE_URL;
import static com.technoelevate.payment.common.PaymentConstants.CANCEL_URL;
import static com.technoelevate.payment.common.PaymentConstants.SUCCESS_URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.technoelevate.payment.dto.OrderDto;
import com.technoelevate.payment.response.ResponseMessage;
import com.technoelevate.payment.service.PaypalService;

@RestController
public class PaypalController {

	@Autowired
	private PaypalService service;

	@PostMapping("/pay")
	public ResponseEntity<ResponseMessage> payment(@RequestBody OrderDto orderDto) {
		Payment payment = service.createPayment(orderDto, BASE_URL + CANCEL_URL, BASE_URL + SUCCESS_URL);
		for (Links link : payment.getLinks()) {
			if (link.getRel().equals("approval_url"))
				return new ResponseEntity<>(new ResponseMessage(false, "Processing the Transection!!!", link.getHref()), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ResponseMessage(true, "SomeThing Went Wrong", null), HttpStatus.BAD_REQUEST);
	}

	@GetMapping(value = SUCCESS_URL)
	public ResponseEntity<ResponseMessage> successPay(@RequestParam("paymentId") String paymentId,
			@RequestParam("payerID") String payerId) {
		Payment payment = service.executePayment(paymentId, payerId);
		System.out.println(payment.toJSON());
		if (payment.getState().equals("approved"))
			return new ResponseEntity<>(new ResponseMessage(false, "Payment Successfully Done!!!", paymentId), HttpStatus.OK);
		return new ResponseEntity<>(new ResponseMessage(false, "SomeThing Went Wrong!!!", null), HttpStatus.OK);
	}

	@GetMapping(value = CANCEL_URL)
	public ResponseEntity<ResponseMessage> cancelPay(@RequestParam("token") String token) {
		System.out.println("Fail To Process The Payment!!!");
		return new ResponseEntity<>(new ResponseMessage(false, "Fail To Process The Payment!!!", null), HttpStatus.OK);
	}

}
