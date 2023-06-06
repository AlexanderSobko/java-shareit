package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserCreationDto> saveUser(@RequestBody @Valid UserCreationDto userCreationDto) {
        return ResponseEntity.status(201).body(userService.saveUser(userCreationDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") long id, @RequestBody UserCreationDto userCreationDto) {
        userCreationDto.setId(id);
        return ResponseEntity.ok(userService.updateUser(userCreationDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @GetMapping
    public ResponseEntity<List<UserCreationDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

}
