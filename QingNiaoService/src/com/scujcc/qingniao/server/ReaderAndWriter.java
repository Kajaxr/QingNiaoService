package com.scujcc.qingniao.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import com.scujcc.qingniao.main.AppServer;

public class ReaderAndWriter implements Runnable {
	private static List pool = new LinkedList();// ��д��
	private static Notifier notifier = Notifier.getNotifier();

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				SelectionKey key;
				synchronized (pool) {
					while (pool.isEmpty()) {
						pool.wait();
					}
					key = (SelectionKey) pool.remove(0);
				}

				// ��ȡ����
				readAndWrite(key);
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * ��ȡ�ͻ��˷�����������
	 * 
	 * @param sc
	 *            �׽�ͨ��
	 */
	private static int BUFFER_SIZE = 1024;

	public static byte[] readRequest(SocketChannel sc) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		int size = 0;// messge size
		int off = 0;
		int r = 0;
		byte[] data = new byte[BUFFER_SIZE * 10];

		buffer.clear();
		r = sc.read(buffer);
		// byte[] mess = new byte[buffer.getInt(0)];
		// buffer.get(mess, 8, 8 + mess.length);
		byte[] req = new byte[off];
		System.arraycopy(data, 0, req, 0, off);
		if (r > 0) {
			System.out.println("mess size " + buffer.getInt(0) + " it id is "
					+ buffer.getInt(4));

			return buffer.array();
		} else
			return null;
	}

	public void readAndWrite(SelectionKey key) {
		try {
			// ��ȡ�ͻ�������
			SocketChannel sc = (SocketChannel) key.channel();
			byte[] clientData = readRequest(sc);
			if (clientData != null) {
				Request request = (Request) key.attachment();
				request.setDataInput(clientData);
				// ����onRead
				notifier.fireOnRead(request);
			//	sc.register(key.selector(), SelectionKey.OP_WRITE, request);
				Response response = new Response(sc);
				// ����onWrite�¼�
				notifier.fireOnWrite((Request) key.attachment(), response);
				// ����onClosed�¼�
				key.interestOps(SelectionKey.OP_READ);
			}
		//	AppServer.processReadAndWriteRequest(key);

		} catch (Exception e) {
			key.cancel();
			notifier.fireOnError("Error occured in Reader: " + e.getMessage());
		}
	}

	/**
	 * ����ͻ�����,�����û��������,�����Ѷ����е��߳̽��д���
	 */
	public static void processRequest(SelectionKey key) {
		synchronized (pool) {
			pool.add(pool.size(), key);
			pool.notifyAll();
		}
	}

	/**
	 * ��������
	 * @deprecated 
	 * @param src
	 *            byte[] Դ��������
	 * @param size
	 *            int ���ݵ�������
	 * @return byte[] ���ݺ������
	 */
	public static byte[] grow(byte[] src, int size) {
		byte[] tmp = new byte[src.length + size];
		System.arraycopy(src, 0, tmp, 0, src.length);
		return tmp;
	}
}
