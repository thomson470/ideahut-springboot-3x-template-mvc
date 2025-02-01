package net.ideahut.springboot.template.listener.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import net.ideahut.springboot.kafka.KafkaReply;
import net.ideahut.springboot.kafka.ReplyMessageListener;
import net.ideahut.springboot.sysparam.dto.SysParamDto;

public class SampleReplyMessageListener {

	public void listen01(ConsumerRecord<String, String> data, KafkaReply<String, SysParamDto> reply) {
		reply.send(createResponse(data.value()));
	}
	
	public void listen02(KafkaReply<String, SysParamDto> reply, ConsumerRecord<String, String> data) {
		reply.send(createResponse(data.value()));
	}
	
	public void listen03(String text, KafkaReply<String, SysParamDto> reply) {
		reply.send(createResponse(text));
	}
	
	public void listen04(KafkaReply<String, SysParamDto> reply, String text) {
		reply.send(createResponse(text));
	}
	
	public SysParamDto listen05(ConsumerRecord<String, String> data) {
		return createResponse(data.value());
	}
	
	public SysParamDto listen06(String text) {
		return createResponse(text);
	}
	
	public ReplyMessageListener<String, String, SysParamDto> listen07() {
		return (data, reply) -> reply.send(createResponse(data.value()));
	}
	
	private SysParamDto createResponse(String text) {
		String suffix = System.nanoTime() + "";
		return new SysParamDto().setSysCode("TEST").setParamCode("PARAM-" + suffix).setValue(text);
	}
	
}
