package com.couponmoa.backend.couponmoanotification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MessageQueueDto {
    private List<String> emailList;
    private String subject;
    private String text;
    private String name;
}
