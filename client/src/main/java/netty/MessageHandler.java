package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import message.AbstractMessage;
import message.FileListRequest;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private final CallBack callBack;

    public MessageHandler(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new FileListRequest());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage message) {
        callBack.call(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Network exception ", cause);
        ctx.close();
    }
}
