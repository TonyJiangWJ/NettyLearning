package com.tony.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TonyJiang on 2018/8/21
 *
 * Shareable 标记这个类的实例之间可以在channel里面共享
 */
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {


    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 读取
     * @param ctx handler上下文
     * @param msg 传输数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf in = (ByteBuf) msg;
        logger.info("Server received:{}", in.toString(CharsetUtil.UTF_8));
        // 将内容重新返回给发送者，此时还没有flush
        ctx.write(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // flush数据给发送者, ChannelFutureListener.CLOSE 当操作完成后关闭通道
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Server exception catch:", cause);
        ctx.close();
    }
}
