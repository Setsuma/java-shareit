package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentOutputDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ModelMapper mapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item item = mapper.map(itemDto, Item.class);
        item.setOwner(userRepository.findById(userId).orElseThrow(() -> new IdNotFoundException("Пользователь не найден")));
        return mapper.map(itemRepository.save(mapper.map(item, Item.class)), ItemDto.class);
    }

    public ItemDto updateItem(ItemDto item, long userId, long itemId) {
        Item existItem = itemRepository.findById(itemId).orElseThrow(() -> new IdNotFoundException("Предмет не найден"));
        if (existItem.getOwner().getId() == userId) {
            if (item.getName() != null) existItem.setName(item.getName());
            if (item.getDescription() != null) existItem.setDescription(item.getDescription());
            if (item.getAvailable() != null) existItem.setAvailable(item.getAvailable());
        } else {
            throw new IdNotFoundException("Предмет не найден");
        }
        return mapper.map(itemRepository.save(existItem), ItemDto.class);
    }

    public List<ItemWithBookingAndCommentsDto> getAllUserItems(long userId) {
        List<Item> items = itemRepository.findByOwner_Id(userId);
        List<ItemWithBookingAndCommentsDto> dtoItems = new ArrayList<>();
        List<Comment> comments = commentRepository.findByItemIn(items);
        for (Item item : items) {
            ItemWithBookingAndCommentsDto dtoItem = mapper.map(item, ItemWithBookingAndCommentsDto.class);
            List<Booking> lastBooking = bookingRepository.findLastBooking(item, LocalDateTime.now(), PageRequest.of(0, 1));
            List<Booking> nextBooking = bookingRepository.findNextBooking(item, LocalDateTime.now(), PageRequest.of(0, 1));
            if (lastBooking.isEmpty()) dtoItem.setLastBooking(null);
            else dtoItem.setLastBooking(mapper.map(lastBooking.get(0), BookingForItemDto.class));
            if (nextBooking.isEmpty()) dtoItem.setNextBooking(null);
            else dtoItem.setNextBooking(mapper.map(nextBooking.get(0), BookingForItemDto.class));
            dtoItem.setComments(comments.stream()
                    .filter(comment -> comment.getItem().equals(item))
                    .map(comment -> mapper.map(comment, CommentOutputDto.class))
                    .collect(Collectors.toList()));
            dtoItems.add(dtoItem);
        }
        return dtoItems;
    }

    public ItemWithBookingAndCommentsDto getItemById(long userid, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IdNotFoundException("Предмет не найден"));
        ItemWithBookingAndCommentsDto dto = mapper.map(item, ItemWithBookingAndCommentsDto.class);
        if (item.getOwner().getId() == userid) {
            List<Booking> lastBooking = bookingRepository.findLastBooking(item, LocalDateTime.now(), PageRequest.of(0, 1));
            List<Booking> nextBooking = bookingRepository.findNextBooking(item, LocalDateTime.now(), PageRequest.of(0, 1));
            if (lastBooking.isEmpty()) dto.setLastBooking(null);
            else dto.setLastBooking(mapper.map(lastBooking.get(0), BookingForItemDto.class));
            if (nextBooking.isEmpty()) dto.setNextBooking(null);
            else dto.setNextBooking(mapper.map(nextBooking.get(0), BookingForItemDto.class));
        }
        List<CommentOutputDto> comments = commentRepository.findAllByItem(item).stream()
                .map(comment -> mapper.map(comment, CommentOutputDto.class))
                .collect(Collectors.toList());
        dto.setComments(comments);
        return dto;
    }

    public List<ItemDto> getSearchItems(String text) {
        if (text.isBlank()) return Collections.emptyList();
        return itemRepository.search(text)
                .stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CommentOutputDto createComment(CommentDto commentDto, long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IdNotFoundException("Предмет не найден"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IdNotFoundException("Пользователь не найден"));
        if (bookingRepository.existBooking(user, item, LocalDateTime.now())) {
            Comment comment = mapper.map(commentDto, Comment.class);
            comment.setItem(item);
            comment.setAuthor(user);
            return mapper.map(commentRepository.save(comment), CommentOutputDto.class);
        } else throw new UnavailableException("Отзывы могут оставлять только те люди, которые уже пользовались вещью");
    }
}
