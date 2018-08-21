package com.tony.server;

import com.tony.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author TonyJiang on 2018/8/21
 */
public class EchoServer {

    private final int port;
    private final Logger logger = LoggerFactory.getLogger(EchoServer.class);

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    // 指定使用nio的channel
                    .channel(NioServerSocketChannel.class)
                    // 指定端口
                    .localAddress(new InetSocketAddress(port))
                    // 添加EchoServerHandler到Channel的ChannelPipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            // 绑定服务器sync等待服务器关闭
            ChannelFuture future = bootstrap.bind().sync();
            logger.info("Server started and listen on {}", future.channel().localAddress());
            // 关闭channel
            future.channel().closeFuture().sync();
        } finally {
            // 关闭EventLoopGroup释放所有资源
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        EchoServer echoServer = new EchoServer(1201);
        echoServer.start();
    }

}
