package com.scujcc.qingniao.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.scujcc.qingniao.domain.WillRemoveUser;

/**
 * ������Ϣ�����߳�
 * 
 * @author ����Բ
 * 
 */
public class AliveManager implements Runnable {
	private static List<Integer> alives = new ArrayList<Integer>();//�����û�����
	private static Map<Integer, WillRemoveUser> willRemoves = new LinkedHashMap<Integer, WillRemoveUser>();//��Ҫ�����û�����
	private static int DELAY = 30000;// ������ʱ������

	private static void addLive(int userId) {
		synchronized (willRemoves) {
			if (willRemoves.containsKey(userId))
				willRemoves.remove(userId);
		}

		synchronized (alives) {
			if (!alives.contains(userId)) {
				alives.add(userId);
			}
		}

	}

	private static void goRemove(int userId) {
		synchronized (willRemoves) {
			if (!willRemoves.containsKey(userId)) {
				WillRemoveUser removeUser = new WillRemoveUser();
				removeUser.setUserId(userId);
				removeUser.setApplyRemoveTime(new Date().getTime());
				willRemoves.put(userId, removeUser);
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				synchronized (willRemoves) {
					long nowDate = new Date().getTime();
					for (Entry<Integer, WillRemoveUser> entry : willRemoves
							.entrySet()) {
						if (nowDate - entry.getValue().getApplyRemoveTime() < DELAY) {
							break;
						} else {
							synchronized (alives) {
								alives.remove(entry.getKey());
							}
							willRemoves.remove(entry.getKey());
						}
					}
				}
				Thread.sleep(30000);
			} catch (Exception exception) {

			}
		}
	}

	public static void processAddLive(int userId) {
		try {
			addLive(userId);
		} catch (Exception exception) {

		}
	}

	public static void processRemoveLive(int userId) {
		try {
			goRemove(userId);
		} catch (Exception exception) {

		}
	}
}
