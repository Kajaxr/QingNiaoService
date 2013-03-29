package com.scujcc.qingniao.handler;


import java.util.*;
import java.text.DateFormat;

import com.scujcc.qingniao.event.EventAdapter;
import com.scujcc.qingniao.server.Request;
import com.scujcc.qingniao.server.Response;

/**
 * ʱ���ѯ������
 */
public class TimeHandler extends EventAdapter {
    public TimeHandler() {
    }

    public void onWrite(Request request, Response response) throws Exception {
        String command = new String(request.getDataInput());
        System.out.println("get "+command+" and start to write to client");
        String time = null;
        Date date = new Date();

        // �жϲ�ѯ����
        if (command.equals("GB")) {
            // ���ĸ�ʽ
            DateFormat cnDate = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.FULL, Locale.CHINA);
            time = cnDate.format(date);
        }
        else {
            // Ӣ�ĸ�ʽ
            DateFormat enDate = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.FULL, Locale.US);
            time = enDate.format(date);
        }
       String s= "hello i get you meess.";  
        response.send(s.getBytes());
    }
}
