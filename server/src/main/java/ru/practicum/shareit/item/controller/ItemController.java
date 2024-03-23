package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody ItemDto itemDto,
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
            @RequestParam(name = "from") Integer from,
            @RequestParam(name = "size") Integer size) {
        return ResponseEntity.ok(itemService.getAllUserItems(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getSearchItems(@RequestParam String text,
                                                        @RequestParam(name = "from") Integer from,
                                                        @RequestParam(name = "size") Integer size) {
        return ResponseEntity.ok(itemService.getSearchItems(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentOutputDto> createComment(@RequestBody CommentDto commentDto,
                                                          @RequestHeader("X-Sharer-User-Id") long userId,
                                                          @PathVariable long itemId) {
        return ResponseEntity.ok(itemService.createComment(commentDto, userId, itemId));
    }
}
