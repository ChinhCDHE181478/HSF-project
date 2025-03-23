package com.hsf302.jpa.supermarket.controller;

import com.hsf302.jpa.supermarket.DTO.ResponseMessage;
import com.hsf302.jpa.supermarket.config.VNPayConfig;
import com.hsf302.jpa.supermarket.model.*;
import com.hsf302.jpa.supermarket.service.*;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {
    final CartService cartService;
    final AccountService accountService;
    final OrderService orderService;
    final ProductService productService;
    final String[] paymentMethods = {"COD", "VNPay"};
    final VNPayService vnPayService;
    @Autowired
    public OrderController( VNPayService vnPayService ,CartService cartService, AccountService accountService, OrderService orderService, ProductService productService) {
        this.cartService = cartService;
        this.accountService = accountService;
        this.orderService = orderService;
        this.productService = productService;
        this.vnPayService = vnPayService;
    }

    private static final double EXCHANGE_RATE = 25000.0; // Tỷ giá USD -> VNĐ (cập nhật theo thực tế)

    public static long convertUsdToVnd(double usd) {
        return Math.round(usd * EXCHANGE_RATE);
    }

    @GetMapping("/success")
    String success(@Nullable @SessionAttribute(value = "accEmail", required = false) String email,
                   @SessionAttribute(value = "isLogin", required = false) Boolean isLogin,
                   @Nullable @SessionAttribute(value = "sCart", required = false) Cart sCart,
                   Model model, HttpServletRequest request){
        if (email == null) {
            return "redirect:/account/login";
        }
        Account account = accountService.getAccount(email);
        Order requestOrder = orderService.getOrder((Long) request.getSession().getAttribute("orderId"));
        if (requestOrder == null) {
            return "redirect:/";
        } else {
//            request.getSession().removeAttribute("order");
        }
        if (isLogin == null || !isLogin) {
            model.addAttribute("isLogin", false);
        } else {
            model.addAttribute("isLogin", true);
        }
        model.addAttribute("user", account);
        model.addAttribute("order", requestOrder);
        model.addAttribute("sCart", sCart);
        model.addAttribute("navActive", "order");
        return "order-detail";
    }

    @PostMapping("/checkout")
    ResponseEntity<ResponseMessage<String>> checkout(@Nullable @SessionAttribute(value = "accEmail", required = false) String email,
                                                     @RequestBody Map<String, Object> body,
                                                     @Nullable @SessionAttribute(value = "sCart", required = false) Cart sCart,
                                                     HttpServletRequest request) throws UnsupportedEncodingException {
        if (email == null) {
            return ResponseEntity.ok((new ResponseMessage<>(Response.SC_UNAUTHORIZED, "Please reload and try again")));
        }
        Cart cart = sCart;
        Order order = cart.moveCartToOrder();
        String name = (String) body.get("name");
        String phone = (String) body.get("phone");
        String address = body.get("address") + ", " + (body.get("city") == null ? "" : (String) body.get("city")) + ", " + ((String) body.get("country") == null ? "" : (String) body.get("country"));
        String note = body.get("note") == null ? "" : (String) body.get("note");
        String paymentMethod = paymentMethods[Integer.parseInt(body.get("paymentMethod").toString())-1];
        if (name == null || phone == null || address.length() < 10) {
            return ResponseEntity.ok((new ResponseMessage<>(Response.SC_BAD_REQUEST, "Please fill in all required fields")));
        }
        if (cart.getItems().isEmpty()) {
            return ResponseEntity.ok((new ResponseMessage<>(Response.SC_BAD_REQUEST, "Your cart is empty, please try again")));
        }
        // check if quantity is enough with stock
        for (SelectedItem item : cart.getItems()) {
            if (item.getQuantity() > item.getProduct().getStock()) {
                return ResponseEntity.ok((new ResponseMessage<>(Response.SC_BAD_REQUEST, "Sorry, " + item.getProduct().getName() + " is not enough in stock, please try again")));
            }
        }
        /* ----------------- ORDER SUCCESS PROCESS ----------------- */
        // update product quantity
        for (SelectedItem item : cart.getItems()) {
            Product product = item.getProduct();
            int qty = item.getQuantity();
            product.setStock(product.getStock() - qty);
            product.setSold(product.getSold() + qty);
            productService.saveProduct(product);
        }
        String txn = null;
        if(!paymentMethod.equals("COD")) {
            txn = VNPayConfig.getRandomNumber(8);
        }
        accountService.updateAddress(email, address);
        order.setData(name, phone, address, note, paymentMethod, "Pending", txn, null, null, null);
        Order newOrder = orderService.saveOrder(order);

        // set with new items list
        newOrder.setItems(new ArrayList<>(cart.getItems()));
        cart.getItems().forEach(item -> item.setSelectedList(newOrder));
        orderService.saveOrder(newOrder);
        cart.clearCart();
        cartService.saveCart(cart);
        request.getSession().setAttribute("orderId", newOrder.getId());
        request.getSession().setAttribute("sCart", cart);

        if(!paymentMethod.equals("COD")) {
            Long amount = convertUsdToVnd(order.getTotalPrice());
            String url = vnPayService.createPaymentUrl(txn, amount, request);
            return ResponseEntity.ok((new ResponseMessage<>(Response.SC_OK, url)));
        }
        return ResponseEntity.ok((new ResponseMessage<>(Response.SC_OK, "Checkout successfully")));
    }

    @GetMapping("/tracking")
    String trackOrder(@RequestParam Long orderId,
                      @Nullable @SessionAttribute(value = "isLogin", required = false) Boolean isLogin,
                      Model model) {
        Order order = orderService.getOrder(orderId);

        if (isLogin == null || !isLogin) {
            model.addAttribute("isLogin", false);
        } else {
            model.addAttribute("isLogin", true);
        }
        if (order == null) {
            System.out.println("Order not found!");
            return "redirect:/";
        }
        model.addAttribute("order", order);
        System.out.println("Order name: " + order.getName());

        return "order-tracking";
    }

    @PostMapping("/update-status")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status) {
        Order order = orderService.getOrder(orderId);

        if (order != null) {
            order.setStatus(status);
            orderService.saveOrder(order);
        }

        return "redirect:/order/tracking?orderId=" + orderId;
    }

    @GetMapping("/after-payment")
    public String afterPayment(
                               @RequestParam Map<String, String> params) {
        // Kiểm tra trạng thái giao dịch
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_BankTranNo = params.get("vnp_BankTranNo");
        String vnp_TransactionNo = params.get("vnp_TransactionNo");
        String vnp_PayDate = params.get("vnp_PayDate");
        String vnp_TxnRef = params.get("vnp_TxnRef");
        LocalDateTime payDateTime = LocalDateTime.parse(vnp_PayDate, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        Order p = orderService.getOrderByTxn(vnp_TxnRef).get(0);
        Long id = p.getId();
        // Xử lý logic kiểm tra thành công hay thất bại
        if ("00".equals(vnp_ResponseCode)) {
            p.setPaymentAt(payDateTime);
            p.setVnpBankTranNo(vnp_BankTranNo);
            p.setVnpTransactionNo(vnp_TransactionNo);
            orderService.saveOrder(p);
        } else {
            // Thanh toán thất bại hoặc bị hủy
            orderService.deleteOrder(p.getId());
        }

        return "redirect:/order/success"; // Điều hướng về trang xác nhận đơn hàng
    }

}
