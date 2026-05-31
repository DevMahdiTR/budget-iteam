package com.iteam.buget.core.mapper;

import com.iteam.buget.core.comment.Comment;
import com.iteam.buget.core.dto.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserMapper userMapper;

    public CommentResponse toResponse(Comment c) {
        return CommentResponse.builder()
                .id(c.getId())
                .content(c.getContent())
                .author(userMapper.toResponse(c.getAuthor()))
                .createdAt(c.getCreatedAt())
                .build();
    }
}
