package server.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
public class Message implements Serializable {
    private int id;
    private String roomName;
    private OffsetDateTime time;
    private String nickname;
    private String content;
}
