package net.ideahut.springboot.template.listener.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("sampleStringBeanMethodMessageListener")
public class SampleStringBeanMethodMessageListener {

	public MessageListener<String, String> listener() {
		return (ConsumerRecord<String, String> data) -> log.info("SampleStringBeanMethodMessageListener - listener: \n{}", data);
	}
	
	public void message(ConsumerRecord<String, String> data) {
		log.info("SampleStringBeanMethodMessageListener - message: \n{}", data);
	}

}
