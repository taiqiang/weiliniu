package com.hivetv.shop_rabbitmq.topic;

import com.hivetv.shop_rabbitmq.util.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class Send {

    private final static String EXCHANGE_NAME = "test_exchange_topic";

    public static void main(String[] argv) throws Exception {
        // 获取到连接以及mq通道
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        // 声明exchange
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        for (int i = 0; i < 50; i++) {
            // 消息内容
            String message = "删除商品 id = 1000_" + i;
            channel.basicPublish(EXCHANGE_NAME, "item.delete", null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
            Thread.sleep(i * 10);
        }
        channel.close();
        connection.close();
    }
}