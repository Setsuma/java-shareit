package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestOutputDto addItemRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                               @RequestHeader(value = "X-Sharer-User-Id") Long requesterId) {
        return itemRequestService.addItemRequest(itemRequestDto, requesterId);
    }

    @GetMapping
    public List<ItemRequestWithAnswersDto> getItemRequestsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return itemRequestService.getItemRequestsByUserId(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithAnswersDto> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return itemRequestService.getAllRequests(requesterId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestWithAnswersDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
