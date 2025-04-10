package com.couponmoa.backend.couponmoanotification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MessageQueueDto {
    private String couponName;
    private List<String> emailList;
}
