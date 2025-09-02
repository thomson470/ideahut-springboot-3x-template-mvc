package net.ideahut.springboot.template.listener.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SampleBytesClassMessageListener {
	
	public static MessageListener<String, byte[]> staticListener() {
		return (ConsumerRecord<String, byte[]> data) -> log.info("SampleBytesClassMessageListener - staticListener: \n{}", data);
	}
	
	public static void staticMessage(ConsumerRecord<String, byte[]> data) {
		log.info("SampleBytesClassMessageListener - staticMessage: \n{}", data);
	}
	
	public MessageListener<String, byte[]> nonStaticListener() {
		return (ConsumerRecord<String, byte[]> data) -> log.info("SampleBytesClassMessageListener - nonStaticListener: \n{}", data);
	}
	
	public void nonStaticMessage(ConsumerRecord<String, byte[]> data) {
		log.info("SampleBytesClassMessageListener - nonStaticMessage: \n{}", data);
	}
	
}
