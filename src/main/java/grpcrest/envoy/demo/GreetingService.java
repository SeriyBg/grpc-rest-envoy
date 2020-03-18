package grpcrest.envoy.demo;

import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import grpcrest.envoy.demo.proto.Greeting;
import grpcrest.envoy.demo.proto.GreetingResponse;
import grpcrest.envoy.demo.proto.GreetingServiceGrpc;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.lite.ProtoLiteUtils;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
public class GreetingService extends GreetingServiceGrpc.GreetingServiceImplBase {

    private static final Metadata.Key<com.google.rpc.Status> STATUS_DETAILS_KEY =
            Metadata.Key.of(
                    "grpc-status-details-bin",
                    ProtoLiteUtils.metadataMarshaller(com.google.rpc.Status.getDefaultInstance()));

    @Override
    public void greet(Greeting request, StreamObserver<Empty> responseObserver) {
        if (request.getHello().equalsIgnoreCase("error")) {
            com.google.rpc.Status status = com.google.rpc.Status.newBuilder()
                    .setCode(Status.NOT_FOUND.getCode().value())
                    .setMessage("Messages")
                    .addDetails(Any.pack(request))
                    .build();
            Metadata metadata = new Metadata();
            metadata.put(STATUS_DETAILS_KEY, status);
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(status.getMessage())
                    .asRuntimeException(metadata));
            return;
        }
        System.out.println("Hello " + request.getHello());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void greetResponse(Greeting request, StreamObserver<GreetingResponse> responseObserver) {
        if (request.getHello().equalsIgnoreCase("error")) {
            com.google.rpc.Status status = com.google.rpc.Status.newBuilder()
                    .setCode(5)
                    .setMessage("Messages")
                    .addDetails(Any.newBuilder()
                            .setValue(GreetingResponse.newBuilder()
                                    .setHello("Hello")
                                    .setName(request.getHello())
                                    .build().toByteString())
                            .build())
                    .build();
            Metadata metadata = new Metadata();
            metadata.put(STATUS_DETAILS_KEY, status);
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(status.getMessage())
                    .asRuntimeException(metadata));
            return;
        }
        responseObserver.onNext(GreetingResponse.newBuilder()
                .setHello("Hello")
                .setName(request.getHello())
                .build());
        responseObserver.onCompleted();
    }
}
