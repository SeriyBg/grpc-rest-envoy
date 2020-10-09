package grpcrest.envoy.demo;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Repository;

@Repository
public class SongRepository {

    private static final List<Song> ALL_SONGS = Lists.newArrayList(
            Song.of("Bohemian Rhapsody", "A Night at the Opera", LocalDate.of(1975, 10, 31)),
            Song.of("Love of My Life", "A Night at the Opera", LocalDate.of(1975, 11, 21)),
            Song.of("You're My Best Friend", "A Night at the Opera", LocalDate.of(1976, 5, 18)),
            Song.of("Another One Bites the Dust", "The Game", LocalDate.of(1980, 6, 30)),
            Song.of("Don't Stop Me Now", "Jazz", LocalDate.of(1979, 6, 5)),
            Song.of("Crazy Little Thing Called Love", "The Game", LocalDate.of(1979, 10, 5)),
            Song.of("We Will Rock You", "News of the World", LocalDate.of(1977, 10, 7)),
            Song.of("We Are the Champions", "News of the World", LocalDate.of(1977, 10, 7)),
            Song.of("I Want It All", "The Miracle", LocalDate.of(1989, 5, 2)),
            Song.of("Spread Your Wings", "News of the World", LocalDate.of(1978, 2, 10))
    );

    private static final Map<String, String> SONG_LYRICS = Map.of(
            "Bohemian Rhapsody", "Mama, just killed a man\n" +
                    "Put a gun against his head\n" +
                    "Pulled my trigger, now he's dead\n" +
                    "Mama, life had just begun\n" +
                    "But now I've gone and thrown it all away\n" +
                    "Mama, ooh\n" +
                    "Didn't mean to make you cry\n" +
                    "If I'm not back again this time tomorrow\n" +
                    "Carry on, carry on\n" +
                    "As if nothing really matters\n",
            "Love of My Life", "Love of my life, you've hurt me\n" +
                    "You've broken my heart and now you leave me\n" +
                    "Love of my life, can't you see?\n" +
                    "Bring it back, bring it back\n" +
                    "Don't take it away from me, because you don't know\n" +
                    "What it means to me\n"
    );

    Collection<Song> findAll() {
        return ALL_SONGS;
    }

    Collection<Song> findByName(String name) {
        return ALL_SONGS.stream().filter(song -> song.getName().equals(name)).collect(Collectors.toList());
    }

    Collection<Song> findByAlbum(String album) {
        return ALL_SONGS.stream().filter(song -> song.getAlbum().equals(album)).collect(Collectors.toList());
    }

    Stream<String> findLyrics(String songName) {
        return SONG_LYRICS.getOrDefault(songName, "").lines();
    }

    void createNewSong(String name, String album) {
        ALL_SONGS.add(Song.of(name, album, LocalDate.now()));
    }
}
