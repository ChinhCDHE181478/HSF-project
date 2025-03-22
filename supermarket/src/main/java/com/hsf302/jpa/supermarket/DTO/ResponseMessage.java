package com.hsf302.jpa.supermarket.DTO;

import lombok.*;

import java.util.Optional;

@Data
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class ResponseMessage<T> {
    @NonNull
    private Integer status;
    @NonNull
    private String message;
    private Optional<T> data;
}
