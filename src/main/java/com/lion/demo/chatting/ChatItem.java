package com.lion.demo.chatting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatItem {
    private int isMine; // 1: 내가쓴 글, 0: 상대방이 쓴 글
    private String message;
    private String timeStr;
    private int hasRead;

}
