package com.jingeore.chatting;

import lombok.Data;

@Data
public class ChattingMessageForm {
    private String message;

    private Long productId;

    private Long writerId;
}
