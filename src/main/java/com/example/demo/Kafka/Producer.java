package com.example.demo.Kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * HKj
 */
public class Producer {
    private static final String brokerList="106.14.250.7:9092";
    private static final String topic="test";
    private static final String topic1="topic1";
    public static void main(String[] args){
        Properties properties=new Properties();
        //设置key序列化器
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        //设置重试次数
        properties.put(ProducerConfig.RETRIES_CONFIG,10);
        //设置值序列化器
        properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        //设置集群地址
        properties.put("bootstrap.servers",brokerList);

        KafkaProducer<String,String> producer=new KafkaProducer<String, String>(properties);
        ProducerRecord<String,String> record=new ProducerRecord<>(topic1,"kafka-demo","hello,kafka");
        try{
            producer.send(record);
        }catch (Exception e){
            e.printStackTrace();
        }
        producer.close();

    }
}
