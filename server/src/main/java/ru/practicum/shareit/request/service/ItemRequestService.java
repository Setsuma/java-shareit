package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestOutputDto addItemRequest(ItemRequestDto itemRequestDto, Long requesterId);

    List<ItemRequestWithAnswersDto> getItemRequestsByUserId(Long requesterId);

    List<ItemRequestWithAnswersDto> getAllRequests(Long requesterId, Integer from, Integer size);

    ItemRequestWithAnswersDto getItemRequestById(Long userId, Long requestId);
}
