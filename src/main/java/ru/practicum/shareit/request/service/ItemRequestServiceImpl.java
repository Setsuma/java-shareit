package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ModelMapper mapper;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestOutputDto addItemRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        ItemRequest itemRequest = mapper.map(itemRequestDto, ItemRequest.class);
        itemRequest.setRequester(userRepository.findById(requesterId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь не найден")));
        return mapper.map(itemRequestRepository.save(itemRequest), ItemRequestOutputDto.class);
    }

    @Override
    public List<ItemRequestWithAnswersDto> getItemRequestsByUserId(Long requesterId) {
        User requester = userRepository.findById(requesterId).orElseThrow(
                () -> new IdNotFoundException("Пользователь не найден"));
        List<ItemRequest> itemRequests = itemRequestRepository.getAllByRequester_IdOrderByCreatedDesc(requesterId);
        Map<Long, List<ItemDto>> items = itemRepository.findByRequestIn(itemRequests)
                .stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(groupingBy(ItemDto::getRequestId, toList()));
        List<ItemRequestWithAnswersDto> result = itemRequests.stream()
                .map(itemRequest -> mapper.map(itemRequest, ItemRequestWithAnswersDto.class))
                .peek(dto -> {
                    List<ItemDto> it = items.get(dto.getId());
                    if (it == null) dto.setItems(Collections.emptyList());
                    else dto.setItems(items.get(dto.getId()));
                })
                .collect(toList());
        return result;
    }

    @Override
    public List<ItemRequestWithAnswersDto> getAllRequests(Long requesterId, Integer from, Integer size) {
        User requester = userRepository.findById(requesterId).orElseThrow(
                () -> new IdNotFoundException("Пользователь не найден"));
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> itemRequests =
                itemRequestRepository.getItemRequestByRequesterIdIsNotOrderByCreated(requesterId, pageable);

        Map<Long, List<ItemDto>> items = itemRepository.findByRequestIn(itemRequests)
                .stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(groupingBy(ItemDto::getRequestId, toList()));

        List<ItemRequestWithAnswersDto> result = itemRequests.stream()
                .map(itemRequest -> mapper.map(itemRequest, ItemRequestWithAnswersDto.class))
                .peek(dto -> {
                    List<ItemDto> it = items.get(dto.getId());
                    if (it == null) dto.setItems(Collections.emptyList());
                    else dto.setItems(items.get(dto.getId()));
                })
                .collect(toList());
        return result;
    }

    @Override
    public ItemRequestWithAnswersDto getItemRequestById(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IdNotFoundException("Пользователь не найден"));
        ItemRequest result = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new IdNotFoundException("Запрос не найден"));
        List<ItemDto> items = itemRepository.findByRequest_Id(requestId)
                .stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
        ItemRequestWithAnswersDto dto = mapper.map(result, ItemRequestWithAnswersDto.class);
        if (items == null) dto.setItems(Collections.emptyList());
        else dto.setItems(items);
        return dto;
    }
}
