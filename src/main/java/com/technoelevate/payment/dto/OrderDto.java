package com.technoelevate.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {

	private double price;
	private String currency;
	private String method;
	private String intent;
	private String description;
	private String requestId;
	private String paymentId;
	private String payerId;

}
