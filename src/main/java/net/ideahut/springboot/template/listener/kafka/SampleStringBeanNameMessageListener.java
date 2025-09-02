package net.ideahut.springboot.template.listener.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("sampleStringBeanNameMessageListener")
class SampleStringBeanNameMessageListener implements MessageListener<String, String> {

	@Override
	public void onMessage(ConsumerRecord<String, String> data) {
		log.info("SampleBeanNameMessageListener - BeanName: \n{}", data);
	}

}
