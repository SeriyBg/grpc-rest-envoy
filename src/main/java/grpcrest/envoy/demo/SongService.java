package grpcrest.envoy.demo;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import grpcrest.envoy.demo.proto.Song;
import grpcrest.envoy.demo.proto.SongLine;
import grpcrest.envoy.demo.proto.SongNotFoundError;
import grpcrest.envoy.demo.proto.SongRequest;
import grpcrest.envoy.demo.proto.SongServiceGrpc;
import grpcrest.envoy.demo.proto.Songs;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.lite.ProtoLiteUtils;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
@AllArgsConstructor
public class SongService extends SongServiceGrpc.SongServiceImplBase {

    private static final Metadata.Key<com.google.rpc.Status> STATUS_DETAILS_KEY =
            Metadata.Key.of(
                    "grpc-status-details-bin",
                    ProtoLiteUtils.metadataMarshaller(com.google.rpc.Status.getDefaultInstance()));

    private final SongRepository songsRepository;

    @Override
    public void findSong(SongRequest request, StreamObserver<Songs> responseObserver) {
        var songs = songsRepository.findByName(request.getName()).stream()
                .map(this::mapSong)
                .collect(Collectors.toList());
        if (songs.isEmpty()) {
            var any = Any.pack(SongNotFoundError.newBuilder()
                    .setName(request.getName()).build());
            var status = com.google.rpc.Status.newBuilder()
                                .setCode(Status.NOT_FOUND.getCode().value())
                                .setMessage("Messages")
                                .addDetails(any)
                                .build();
            var metadata = new Metadata();
            metadata.put(STATUS_DETAILS_KEY, status);
            responseObserver.onError(Status.NOT_FOUND
                    .asRuntimeException(metadata));
            return;
        }
        responseObserver.onNext(Songs.newBuilder().addAllSongs(songs).build());
        responseObserver.onCompleted();
    }

    @Override
    public void createSong(Song request, StreamObserver<Song> responseObserver) {
        if (request.hasLegacySong()) {
            songsRepository.createNewSong(request.getLegacySong().getSongName(), request.getLegacySong().getSongAlbum());
        } else {
            songsRepository.createNewSong(request.getName(), request.getAlbum());
        }
        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }

    @Override
    public void songLyrics(SongRequest request, StreamObserver<SongLine> responseObserver) {
        songsRepository.findLyrics(request.getName())
                .map(line -> SongLine.newBuilder().setLine(line).build())
                .forEach(v -> {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    responseObserver.onNext(v);
                });
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<SongRequest> buySongs(StreamObserver<Songs> responseObserver) {
        List<Song> songs = new ArrayList<>();
        return new StreamObserver<>() {
            @Override
            public void onNext(SongRequest songRequest) {
                songsRepository.findByName(songRequest.getName())
                        .stream().map(SongService.this::mapSong)
                        .forEach(songs::add);
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(Songs.newBuilder().addAllSongs(songs).build());
                responseObserver.onCompleted();
            }
        };
    }

    private Song mapSong(grpcrest.envoy.demo.Song song) {
        return Song.newBuilder()
                .setName(song.getName())
                .setAlbum(song.getAlbum())
                .setCreatedAt(Timestamp.newBuilder().setSeconds(song.getCreatedDate()
                        .toEpochSecond(LocalTime.now(), ZoneOffset.UTC)))
                .build();
    }
}
