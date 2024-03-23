package ru.practicum.shareit.mapper;

import com.github.rozidan.springboot.modelmapper.ConverterConfigurer;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingDtoToBookingConverter extends ConverterConfigurer<BookingDto, Booking> {
    @Override
    public Converter<BookingDto, Booking> converter() {
        return new AbstractConverter<BookingDto, Booking>() {
            @Override
            protected Booking convert(BookingDto source) {
                Booking destination = Booking.builder()
                        .start(source.getStart())
                        .end(source.getEnd())
                        .build();
                return destination;
            }
        };
    }
}