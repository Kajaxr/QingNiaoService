package com.scujcc.qingniao.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.scujcc.qingniao.domain.Message;

public class Channeler {
	private Vector<Message> messages;// ��Ϣ��
	private boolean isNews;//��Ȥ�û�״̬�Ƿ��и���
	private List<Integer> sendUsers;//״̬���ͼ�
	private Map<Integer, Short> intrestUsers;// ��Ȥ�û�״̬��

	/**
	 * @deprecated
	 * @return ������Ϣ����
	 */
	public synchronized Vector<Message> getMessages() {
		return messages;
	}

	/**
	 * ����Ϣ�ŵ���������Ϣ
	 * 
	 * @param message
	 *            ��Ҫ���ӵ���Ϣ
	 */
	public synchronized void addMessage(Message message) {
		synchronized (messages) {
			if (messages == null) {
				messages = new Vector<Message>();
			}
			messages.add(messages.size(), message);
		}
	}
	/**
	 * ���ŵ��еĵ����Ƴ��������Ϊ10������Ϣ��
	 * @return ���ȷ��͵��������Ϊ10������Ϣ��
	 */
   public synchronized List<Message> getMessage()
   {
	   List<Message> reMess=null;
	   if(messages!=null)
	   {
		   reMess=new ArrayList<Message>();
		   if(messages.size()>=10)
		   {
			   for(int i=0;i<10;i++)
			   {
				   reMess.add(messages.remove(0));
			   }
		   }else
		   {
			   for(int i=messages.size();i>0;i--)
			   {
				   reMess.add(messages.remove(0));
			   }
		   }
		   
	   }
	   return reMess;
   }
	/**
	 * @deprecated
	 * @param messages
	 */
	public synchronized void setMessages(Vector<Message> messages) {
		this.messages = messages;
	}

	public void refeshUsers(int user, short status) {
		
   	}

}
