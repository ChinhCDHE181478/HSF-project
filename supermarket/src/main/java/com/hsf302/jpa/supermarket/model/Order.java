package com.hsf302.jpa.supermarket.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
public class Order extends SelectedList{
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_date")
    private Date orderDate;
    @Column(name = "name")
    private String name;
    @Column(name = "phone")
    private String phone;
    @Column(name = "address")
    private String address;
    @Column(name = "note")
    private String note;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "status")
    private String status;
    @Column(name = "vnp_txn_ref", unique = true)
    private String vnpTxnRef;
    @Column(name = "vnp_bank_tran_no")
    private String vnpBankTranNo;
    @Column(name = "vnp_transaction_no")
    private String vnpTransactionNo;
    @Column(name = "payment_at", columnDefinition = "DATETIME2(0)")
    private LocalDateTime paymentAt;

    public Order() {
    }

    public Order(SelectedList selectedList) {
        super(selectedList);
        this.orderDate = new Date();

    }

    public void setData(String name, String phone, String address, String note, String paymentMethod, String status, String vnpTxnRef,
                        String vnpBankTranNo, String vnpTransactionNo, LocalDateTime paymentAt) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.note = note;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.vnpTxnRef = vnpTxnRef;
        this.vnpBankTranNo = vnpBankTranNo;
        this.vnpTransactionNo = vnpTransactionNo;
        this.paymentAt = paymentAt;
    }

    public String getOrderDate() {
        return String.format("%tI:%<tM %<tp %<td %<tb, %<tY", orderDate);
    }
}
