package grpcrest.envoy.demo;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class Song {
    private final String name;
    private final String album;
    private final LocalDate createdDate;

    static Song of(String name, String album, LocalDate createdDate) {
        return new Song(name, album, createdDate);
    }
}
