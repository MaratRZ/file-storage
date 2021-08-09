package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import message.AbstractMessage;

@Slf4j
public class Network {
    private final CallBack callBack;
    private SocketChannel channel;
    private EventLoopGroup worker;
    public static Network network;

    public Network(CallBack callBack, String host, int port) {
        this.callBack = callBack;
        Thread thread = new Thread(() -> {
            worker = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(worker)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel c) {
                                channel = c;
                                c.pipeline().addLast(
                                        new ObjectEncoder(),
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        new MessageHandler(callBack)
                                );
                            }
                        });
                ChannelFuture future = bootstrap.connect(host, port).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                log.error("", e);
            } finally {
                close();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void writeMessage(AbstractMessage message) {
        channel.writeAndFlush(message);
    }

    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    public void close() {
        worker.shutdownGracefully();
    }

}