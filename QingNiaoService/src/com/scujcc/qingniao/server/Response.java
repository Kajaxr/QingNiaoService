package com.scujcc.qingniao.server;

import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.io.IOException;

/**
 * * <p>
 * Title: ��Ӧ��
 * </p>
 * @author ����Բ
 *
 */
public class Response {
	private SocketChannel sc;

	public Response(SocketChannel sc) {
		this.sc = sc;
	}

	/**
	 * ��ͻ���д����
	 * 
	 * @param data  byte[]������Ӧ����
	 */
	public void send(byte[] data) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(data.length);
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		sc.write(buffer);
		buffer.compact();

	}
}
