package com.manish.OrderService;

import com.manish.OrderService.exception.CustomException;
import com.manish.OrderService.external.client.PaymentService;
import com.manish.OrderService.external.client.ProductService;
import com.manish.OrderService.external.request.PaymentRequest;
import com.manish.OrderService.external.response.PaymentResponse;
import com.manish.OrderService.model.OrderRequest;
import com.manish.OrderService.model.OrderResponse;
import com.manish.OrderService.model.PaymentMode;
import com.manish.OrderService.repository.OrderRepository;
import com.manish.OrderService.service.OrderService;
import com.manish.OrderService.service.OrderServiceImpl;
import com.manish.PriductService.model.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.manish.OrderService.entity.Order;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceApplicationTests {
	@Mock
	private OrderRepository orderRepository;

	@Mock
	private ProductService productService;

	@Mock
	private PaymentService paymentService;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	OrderService orderService=new OrderServiceImpl();

	@DisplayName("Get Order -  Success scenario")
		@Test
		void test_When_Order_Success() {
			Order order
					= mockOrderObject();
			when(orderRepository.findById(anyLong()))
					.thenReturn(Optional.of(order));
			when(restTemplate.getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(),
					ProductResponse.class)).thenReturn(getMockProductResponse());
			when(restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(),
					PaymentResponse.class)).thenReturn(getMockPaymentResponse());
			OrderResponse  orderResponse = orderService.getOrder("1");
			verify(orderRepository,times(1)).findById(anyLong());
			verify(restTemplate,times(1)).getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(), ProductResponse.class);
			verify(restTemplate,times(1)).getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(),PaymentResponse.class);

			assertNotNull(orderResponse);
			assertEquals(order.getId(),orderResponse.getOrderId());
		}
	@DisplayName("Get Orders - Failure Scenario")
		@Test
	void test_When_Order_NOT_FOUND_then_Not_Found() {
	when(orderRepository.findById(1L)).thenReturn(Optional.ofNullable(null));
		CustomException customException = assertThrows(CustomException.class, () -> orderService.getOrder("1"));
		assertEquals("NOT_FOUND",customException.getErrorCode());
		assertEquals(404,customException.getStatus());
		verify(orderRepository,times(1)).findById(anyLong());
	}

	@DisplayName("Place Order - Success")
	@Test
	void test_When_Place_Order_Success(){
		Order order= mockOrderObject();
	OrderRequest orderRequest=mockOrderRequest();

		when(orderRepository.save(any(Order.class)))
				.thenReturn(order);
		when(productService.reduceQuantity(anyLong(),anyLong()))
				.thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
		when(paymentService.doPayment(any(PaymentRequest.class)))
				.thenReturn(new ResponseEntity<Long>(1L,HttpStatus.OK));
		Long orderId=orderService.placeOrder(orderRequest);

		verify(orderRepository,times(2)).save(any());
		verify(productService,times(1)).reduceQuantity(anyLong(),anyLong());
		verify(paymentService,times(1)).doPayment(any(PaymentRequest.class));
		assertEquals(order.getId(),orderId);
	}
	@DisplayName("Place Order - Payment Failed Scenario")
	@Test
	void test_When_place_Order_Payment_Fails_then_Order_Placed(){
		Order order= mockOrderObject();
		OrderRequest orderRequest=mockOrderRequest();

		when(orderRepository.save(any(Order.class)))
				.thenReturn(order);
		when(productService.reduceQuantity(anyLong(),anyLong()))
				.thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
		when(paymentService.doPayment(any(PaymentRequest.class)))
				.thenThrow(new RuntimeException());
		Long orderId=orderService.placeOrder(orderRequest);
		verify(orderRepository,times(2)).save(any());
		verify(productService,times(1)).reduceQuantity(anyLong(),anyLong());
		verify(paymentService,times(1)).doPayment(any(PaymentRequest.class));
		assertEquals(order.getId(),orderId);
	}
	private OrderRequest mockOrderRequest() {
		return OrderRequest.builder()
				.productId(1L)
				.totleAmount(1000L)
				.paymentMode(PaymentMode.CASE)
				.quantity(2L)
				.build();
	}

	private PaymentResponse getMockPaymentResponse() {
	return PaymentResponse.builder().
		paymentId(1L).
		paymentDate(Instant.now()).
		paymentMode(PaymentMode.CASE).
		amount(2000L).
		orderId(1L).
		status("ACCEPTED").build();
		}

	private ProductResponse getMockProductResponse() {
		return ProductResponse.builder().
		id(2L).
		name("IPhone").
		price(1000l).
		quantity(2L).build();		}

	private Order mockOrderObject() {
	return Order.builder().
		orderDate(Instant.now()).
		orderStatus("CREATED").
		amount(1000l).
		quantity(2l).
		id(1l).
		productId(2L).build();
	}


}
