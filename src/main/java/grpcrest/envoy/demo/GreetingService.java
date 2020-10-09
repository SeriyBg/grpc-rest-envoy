package grpcrest.envoy.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import grpcrest.envoy.demo.proto.Greeting;
import grpcrest.envoy.demo.proto.GreetingResponse;
import grpcrest.envoy.demo.proto.GreetingServiceGrpc;
import io.grpc.Context;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.lite.ProtoLiteUtils;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
public class GreetingService extends GreetingServiceGrpc.GreetingServiceImplBase {

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private static final Metadata.Key<com.google.rpc.Status> STATUS_DETAILS_KEY =
            Metadata.Key.of(
                    "grpc-status-details-bin",
                    ProtoLiteUtils.metadataMarshaller(com.google.rpc.Status.getDefaultInstance()));

    @Override
    @SneakyThrows
    public void greet(Greeting request, StreamObserver<Empty> responseObserver) {

        //req -> search -> segment
        //metadata -> to context -> from context -> metadata
        //req -> search -> new thread(segment)

        System.out.println("Outside: " + HeadersInterceptor.CONTEXT_VALUE.get());
        System.out.println("Outside: " + Context.current());
//        Context.currentContextExecutor(executorService);
        Context.current().fixedContextExecutor(executorService)
                .execute(() -> {
                    System.out.println("Inside: " + HeadersInterceptor.CONTEXT_VALUE.get());
                    System.out.println("Inside: " + Context.current());
                });
//        executorService.submit(() -> {
//            System.out.println("Inside: " + HeadersInterceptor.CONTEXT_VALUE.get());
//            System.out.println("Inside: " + Context.current());
//        });
        System.out.println("Outside: " + Context.current());
        Thread.sleep(500);
        if (request.getHello().equalsIgnoreCase("error")) {
            var status = com.google.rpc.Status.newBuilder()
                    .setCode(Status.INVALID_ARGUMENT.getCode().value())
                    .setMessage("Messages")
                    .addDetails(Any.pack(request))
                    .build();
            var metadata = new Metadata();
            metadata.put(STATUS_DETAILS_KEY, status);
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(status.getMessage())
                    .asRuntimeException(metadata));
            return;
        }
        if (request.getHello().equalsIgnoreCase("error1")) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Error 1")
                    .asRuntimeException());
            return;
        }
        System.out.println("Hello " + request.getHello());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void greetResponse(Greeting request, StreamObserver<GreetingResponse> responseObserver) {
        if (request.getHello().equalsIgnoreCase("error")) {
            var status = com.google.rpc.Status.newBuilder()
                    .setCode(5)
                    .setMessage("Messages")
                    .addDetails(Any.pack(GreetingResponse.newBuilder()
                            .setHello("Hello")
                            .setName(request.getHello())
                            .build()))
                    .build();
            var metadata = new Metadata();
            metadata.put(STATUS_DETAILS_KEY, status);
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(status.getMessage())
                    .asRuntimeException(metadata));
            return;
        }
        responseObserver.onNext(GreetingResponse.newBuilder()
                .setHello("Hello")
                .setName(request.getHello())
                .setTimestamp(Timestamp.newBuilder().setSeconds(1))
                .build());
        responseObserver.onCompleted();
    }
}
