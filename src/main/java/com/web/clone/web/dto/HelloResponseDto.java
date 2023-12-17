package com.web.clone.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
// 자바 17이라서 record로 만들어도 됐을 것 같다.
public class HelloResponseDto {
    private final String name;
    private final int amount;
}
