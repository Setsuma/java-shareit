package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok(itemService.createItem(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId) {
        return ResponseEntity.ok(itemService.updateItem(itemDto, userId, itemId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemWithBookingAndCommentsDto> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                     @PathVariable long itemId) {
        return ResponseEntity.ok(itemService.getItemById(userId, itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemWithBookingAndCommentsDto>> getAllUserItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return ResponseEntity.ok(itemService.getAllUserItems(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getSearchItems(@RequestParam String text,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return ResponseEntity.ok(itemService.getSearchItems(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentOutputDto> createComment(@Valid @RequestBody CommentDto commentDto,
                                                          @RequestHeader("X-Sharer-User-Id") long userId,
                                                          @PathVariable long itemId) {
        return ResponseEntity.ok(itemService.createComment(commentDto, userId, itemId));
    }
}
