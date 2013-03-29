package com.scujcc.qingniao.server;

import java.util.List;
import java.util.LinkedList;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.util.Date;
import java.nio.ByteBuffer;
import java.io.IOException;

import com.scujcc.qingniao.main.AppServer;

/**
 * <p>
 * Title: ���߳�
 * </p>
 * 
 * @author ����Բ
 * 
 */
public class Reader extends Thread {
	private static List<SelectionKey> repool = new LinkedList<SelectionKey>();
	private static Notifier notifier = Notifier.getNotifier();
	private static int BUFFER_SIZE = 1024;
	private static int SIZE_POSiTION = 0;// ��Ϣ�����ڽ����ֽ��еĿ�ʼλ��
	private static int USERID_POSiTION = 4;// �û�Id�ڽ����ֽ��еĿ�ʼλ��
	private static int WANTDO_POSiTION = 8;// �� �������ڽ����ֽ��е¿�ʼλ��
	private static int MESS_POSiTION = 10;// ��Ϣ�ڽ����ֽ��еĿ�ʼλ��
	public Reader() {
	}

	public void run() {
		while (true) {
			try {
				SelectionKey key;
				synchronized (repool) {
					while (repool.isEmpty()) {
						repool.wait();
					}
					key = repool.remove(0);
				}

				// ��ȡ����
				read(key);
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


	public static Request readRequest(SocketChannel sc, Request request)
			throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		buffer.clear();
		int r = 0;
		r = sc.read(buffer);
		if (r > 0) {
			int size = buffer.getInt(SIZE_POSiTION);
			request.setUserId(buffer.getInt(USERID_POSiTION));
			request.setWantDo(buffer.getShort(WANTDO_POSiTION));
			byte[] mesbyte = new byte[size];
			buffer.get(mesbyte, MESS_POSiTION, size);
			request.setDataInput(mesbyte);
			return request;
		} else {
			return null;
		}
	}

	/**
	 * �����������ݶ�ȡ
	 * 
	 * @param key
	 *            SelectionKey
	 */
	public void read(SelectionKey key) {
		try {
			// ��ȡ�ͻ�������
			SocketChannel sc = (SocketChannel) key.channel();
			Request request = (Request) key.attachment();
			request = readRequest(sc, request);
			if (request != null) {

				// request.setDataInput(clientData);
				// ����onRead
				notifier.fireOnRead(request);
				AppServer.processWriteRequest(key);

			}
			// �ύ�����߳̽���д����
			// AppServer.processWriteRequest(key);
		} catch (Exception e) {
			key.cancel();
			notifier.fireOnError("Error occured in Reader: " + e.getMessage());
		}
	}

	/**
	 * ����ͻ�����,�����û��������,�����Ѷ����е��߳̽��д���
	 */
	public static void processRequest(SelectionKey key) {
		synchronized (repool) {
			repool.add(repool.size(), key);
			repool.notifyAll();
		}
	}

	/**
	 * ��������
	 * 
	 * @param src
	 *            byte[] Դ��������
	 * @param size
	 *            int ���ݵ�������
	 * @return byte[] ���ݺ������
	 * @deprecated
	 */
	public static byte[] grow(byte[] src, int size) {
		byte[] tmp = new byte[src.length + size];
		System.arraycopy(src, 0, tmp, 0, src.length);
		return tmp;
	}
}
