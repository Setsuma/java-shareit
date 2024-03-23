package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestOutputDto> addItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                                               @RequestHeader(value = "X-Sharer-User-Id") Long requesterId) {
        return ResponseEntity.ok(itemRequestService.addItemRequest(itemRequestDto, requesterId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestWithAnswersDto>> getItemRequestsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return ResponseEntity.ok(itemRequestService.getItemRequestsByUserId(requesterId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestWithAnswersDto>> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @RequestParam(name = "from") Integer from,
            @RequestParam(name = "size") Integer size) {
        return ResponseEntity.ok(itemRequestService.getAllRequests(requesterId, from, size));
    }

    @GetMapping("{requestId}")
    public ResponseEntity<ItemRequestWithAnswersDto> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                        @PathVariable Long requestId) {
        return ResponseEntity.ok(itemRequestService.getItemRequestById(userId, requestId));
    }
}
