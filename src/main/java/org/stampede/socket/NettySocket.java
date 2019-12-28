/*
 * Copyright 2012 The Netty Project The Netty Project licenses this file to you under the Apache
 * License, version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at: http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.stampede.socket;

import java.io.IOException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.CharsetUtil;

/**
 * Echoes back any received data from a client.
 */
public final class NettySocket extends AbstractSocket {

	static final ByteBuf OK_BUF = Unpooled.copiedBuffer(OK);
	static final ByteBuf NOTOK_BUF = Unpooled.copiedBuffer(NOTOK);

	@Sharable
	class EchoAdapter extends ChannelInboundHandlerAdapter {

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object message) {
			String path;
			ByteBuf buffer = (ByteBuf)message;
			try {
				path = getPath(new ByteBufInputStream((ByteBuf)message)).trim();
			} catch (IOException e) {
				path = "";
			}
			OK_BUF.retain();
			ctx.write(OK_BUF);
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) {
			ctx.flush();
			ctx.close();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			// Close the connection when an exception is raised.
			cause.printStackTrace();
			ctx.close();
		}
	}

	public NettySocket() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		try {
			this.main();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	ChannelFuture f;
	EventLoopGroup bossGroup;
	EventLoopGroup workerGroup;

	void main() throws Exception {
		// Configure SSL.
		final SslContext sslCtx;
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		} else {
			sslCtx = null;
		}

		// Configure the server.
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		final ChannelInboundHandlerAdapter serverHandler = new EchoAdapter();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();
							if (sslCtx != null) {
								p.addLast(sslCtx.newHandler(ch.alloc()));
							}
							p.addLast(serverHandler);
						}
					});

			// Start the server.
			f = b.bind(this.PORT).sync();

		} catch (Exception e) {
			logger.error("Failed NettySocket start: " + e.getMessage());
		}
	}

	@Override
	public boolean serve() throws IOException, InterruptedException {
		f.channel().closeFuture().sync();
		return super.serve();
	}

	@Override
	protected void close() throws IOException {
		try {
			f.channel().close().await();
		} catch (InterruptedException e) {
			logger.error("Failed NettySocket close: " + e.getMessage());
		}
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
}
