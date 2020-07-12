package grpcrest.envoy.demo;

import com.google.protobuf.Empty;
import grpcrest.envoy.demo.proto.Song;
import grpcrest.envoy.demo.proto.SongRequest;
import grpcrest.envoy.demo.proto.SongServiceGrpc;
import grpcrest.envoy.demo.proto.Songs;
import io.grpc.Metadata;
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
    public void findSongs(SongRequest request, StreamObserver<Songs> responseObserver) {
//        var status = com.google.rpc.Status.newBuilder()
//                .setCode(Status.INVALID_ARGUMENT.getCode().value())
//                .setMessage("Messages")
//                .addDetails(Any.pack(request))
//                .build();
//        var metadata = new Metadata();
//        metadata.put(STATUS_DETAILS_KEY, status);
//        responseObserver.onError(Status.INVALID_ARGUMENT
//                .withDescription(status.getMessage())
//                .asRuntimeException(metadata));
        super.findSongs(request, responseObserver);
    }

    @Override
    public void createSong(Song request, StreamObserver<Empty> responseObserver) {
        super.createSong(request, responseObserver);
    }
}
