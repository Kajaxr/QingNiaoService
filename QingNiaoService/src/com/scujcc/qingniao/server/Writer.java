package com.scujcc.qingniao.server;

import java.util.List;
import java.util.LinkedList;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;

import com.scujcc.qingniao.main.AppServer;


/**
 * * <p>
 * Title: ��Ӧ�߳�
 * </p>
 * @author ����Բ
 *
 */
public final class Writer extends Thread {
	private static List<SelectionKey> wrpool = new LinkedList<SelectionKey>();
	private static Notifier notifier = Notifier.getNotifier();

	public Writer() {
	}

	/**
	 * �����߳����ط��񷽷�,������������������
	 */
	public void run() {
		while (true) {
			try {
				SelectionKey key;
				synchronized (wrpool) {
					while (wrpool.isEmpty()) {
						wrpool.wait();
					}
					key = wrpool.remove(0);
				}

				// ����д�¼�
				write(key);
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * ������ͻ���������
	 * 
	 * @param key  SelectionKey
	 */
	public void write(SelectionKey key) {
		try {
			SocketChannel sc = (SocketChannel) key.channel();
			Response response = new Response(sc);
			// ����onWrite�¼�
			notifier.fireOnWrite((Request) key.attachment(), response);
			AppServer.processReadRequest(key);
		} catch (Exception e) {
			key.cancel();
			notifier.fireOnError("Error occured in Writer: " + e);
		}
	}

	/**
	 * ����ͻ�����,�����û��������,�����Ѷ����е��߳̽��д���
	 */
	public static void processRequest(SelectionKey key) {
		synchronized (wrpool) {
			wrpool.add(wrpool.size(), key);
			wrpool.notifyAll();
		}
	}
}
