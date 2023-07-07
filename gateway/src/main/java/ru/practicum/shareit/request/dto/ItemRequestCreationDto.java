package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@ToString
public class ItemRequestCreationDto {

    @NotBlank(message = "Описанее не может быть пустым!")
    private String description;

}
