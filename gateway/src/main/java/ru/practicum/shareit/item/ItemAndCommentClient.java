package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;

import java.util.Map;


@Component
public class ItemAndCommentClient extends BaseClient {

    private static final String BASE_PATH = "/items";

    @Autowired
    public ItemAndCommentClient(@Value("${SHAREIT_SERVER_URL}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + BASE_PATH))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveItem(ItemCreationDto itemDto, Long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(ItemCreationDto itemDto, Long itemId, Long userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItem(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }


    public ResponseEntity<Object> getItems(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> searchItems(Long userId, String text, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> saveComment(Long userId, Long itemId, CommentCreationDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

}
