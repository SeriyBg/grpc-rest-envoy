package grpcrest.envoy.demo;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.lognet.springboot.grpc.GRpcGlobalInterceptor;

@GRpcGlobalInterceptor
public class HeadersInterceptor implements ServerInterceptor {

    public static final Context.Key<String> CONTEXT_VALUE =
            Context.keyWithDefault("CTX_VALUE", "");
    private static final Metadata.Key<String> HEADER_VALUE =
            Metadata.Key.of("x-header-value", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata headers,
                                                                 ServerCallHandler<ReqT, RespT> next) {
        Context ctx = Context.current().withValue(CONTEXT_VALUE, headers.get(HEADER_VALUE));
        return Contexts.interceptCall(ctx, call, headers, next);
    }
}
