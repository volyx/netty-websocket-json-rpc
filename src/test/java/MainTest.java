import com.codahale.metrics.Timer;
import org.junit.Assert;
import org.junit.Test;
import com.volyx.rpc.client.RpcClient;
import com.volyx.rpc.common.Handler;
import com.volyx.rpc.common.Request;
import com.volyx.rpc.common.Result;
import com.volyx.rpc.json.Json;
import com.volyx.rpc.server.RpcServer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.util.Date;

import static com.codahale.metrics.MetricRegistry.name;
import static com.volyx.rpc.server.RpcServer.registry;

public class MainTest {

    @Test
    public void test() {
        final int port = findRandomOpenPortOnAllLocalInterfaces();
        RpcServer rpcServer = RpcServer.builder().port(port).build();

        rpcServer.addHandler(new Handler() {
            @Nonnull
            @Override
            public Result execute(@Nonnull Request request) {
                return new Result<>(new Date());
            }

            @Nonnull
            @Override
            public String getName() {
                return "currentdate";
            }
        });

        RpcClient client = RpcClient.builder().port(port).build();
        client.addHandler(new Handler() {
            @Nonnull
            @Override
            public Result execute(@Nonnull Request request) {
                return new Result<>("Boom");
            }

            @Nonnull
            @Override
            public String getName() {
                return "boom";
            }
        });

        Result result = client.send("hello", "param");
        System.out.println(Json.getInstance().toJson(result));
        Assert.assertNotNull(result);
        result = client.send("currentdate", "param");
        System.out.println(Json.getInstance().toJson(result));
        Assert.assertNotNull(result);

    }

//    @Test
    public void perfomance() {
        RpcServer rpcServer = RpcServer.builder().port(8080).build();

        RpcClient client = RpcClient.builder().port(8080).build();
        rpcServer.addHandler(new Handler() {
            int i = 0;


            @Nonnull
            @Override
            public Result execute(@Nonnull Request request) {
                return new Result<>(i++);
            }

            @Nonnull
            @Override
            public String getName() {
                return "inc";
            }
        });

        long start = System.currentTimeMillis();
        int count = 1_000_000;
        Result result = null;
        final Timer timer = registry.timer(name(this.getClass(), "get-requests"));
        for (int i = 0; i < count; i++) {
            final Timer.Context context = timer.time();
            try {
                result = client.send("inc", "param");
            } finally {
                context.stop();
            }
            if (i % 10_000 == 0) {
                System.out.println((i / 10_000) + "%");
            }
        }
        Assert.assertEquals(count - 1, result.getValue());
        System.out.println(result.getValue());
        System.out.println("Time " + (System.currentTimeMillis() - start) + " millis");
    }

    private static int findRandomOpenPortOnAllLocalInterfaces() {
        try (
                ServerSocket socket = new ServerSocket(0)
        ) {
            return socket.getLocalPort();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
