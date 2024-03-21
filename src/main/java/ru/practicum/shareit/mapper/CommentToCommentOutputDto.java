package ru.practicum.shareit.mapper;

import com.github.rozidan.springboot.modelmapper.ConverterConfigurer;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.dto.CommentOutputDto;
import ru.practicum.shareit.item.comment.model.Comment;

@Component
public class CommentToCommentOutputDto extends ConverterConfigurer<Comment, CommentOutputDto> {
    @Override
    public Converter<Comment, CommentOutputDto> converter() {
        return new AbstractConverter<Comment, CommentOutputDto>() {
            @Override
            protected CommentOutputDto convert(Comment source) {
                CommentOutputDto destination = CommentOutputDto.builder()
                        .id(source.getId())
                        .text(source.getText())
                        .authorName(source.getAuthor().getName())
                        .created(source.getCreatedDate())
                        .build();
                return destination;
            }
        };
    }
}