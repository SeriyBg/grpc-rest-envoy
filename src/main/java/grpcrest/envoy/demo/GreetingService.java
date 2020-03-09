package grpcrest.envoy.demo;

import com.google.protobuf.Empty;
import grpcrest.envoy.demo.proto.Greeting;
import grpcrest.envoy.demo.proto.GreetingResponse;
import grpcrest.envoy.demo.proto.GreetingServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
public class GreetingService extends GreetingServiceGrpc.GreetingServiceImplBase {

    @Override
    public void greet(Greeting request, StreamObserver<Empty> responseObserver) {
        System.out.println("Hello " + request.getHello());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void greetResponse(Greeting request, StreamObserver<GreetingResponse> responseObserver) {
        responseObserver.onNext(GreetingResponse.newBuilder()
                .setHello("Hello")
                .setName(request.getHello())
                .build());
        responseObserver.onCompleted();
    }
}
