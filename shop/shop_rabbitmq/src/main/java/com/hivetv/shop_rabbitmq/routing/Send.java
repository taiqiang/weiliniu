package com.hivetv.shop_rabbitmq.routing;

import com.hivetv.shop_rabbitmq.util.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class Send {

    private final static String EXCHANGE_NAME = "test_exchange_direct";

    public static void main(String[] argv) throws Exception {
        // 获取到连接以及mq通道
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        // 声明exchange
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        // 消息内容
        for(int i=0;i<10;i++)
        {
            String message = "id = 1000";
            channel.basicPublish(EXCHANGE_NAME, "delete", null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
            Thread.sleep(1000);
        }
        channel.close();
        connection.close();
    }
}