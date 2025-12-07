package com.example.chatservice.Mapper;

import com.example.chatservice.dto.response.ChatMessage;
import com.example.chatservice.entity.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatMapper {
    public ChatMessage toDto(Message entity) {
        return new ChatMessage(
                entity.getSenderId(),
                entity.getReceiverId(),
                entity.getContent(),
                entity.getTimestamp()
        );
    }

    public List<ChatMessage> toDtoList(List<Message> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}