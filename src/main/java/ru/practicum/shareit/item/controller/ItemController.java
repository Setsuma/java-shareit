package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") int userId, @Valid @RequestBody ItemDto item) {
        return ResponseEntity.ok(itemService.createItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemDto item, @PathVariable int itemId) {
        return ResponseEntity.ok(itemService.updateItem(item, userId, itemId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        return ResponseEntity.ok(itemService.getItemById(userId, itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllUserItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return ResponseEntity.ok(itemService.getAllUserItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getSearchItems(@RequestParam String text) {
        return ResponseEntity.ok(itemService.getSearchItems(text));
    }
}
