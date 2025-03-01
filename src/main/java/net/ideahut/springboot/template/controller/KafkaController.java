package net.ideahut.springboot.template.controller;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.kafka.KafkaHandler;
import net.ideahut.springboot.kafka.KafkaSender;
import net.ideahut.springboot.kafka.ReplyKafkaSender;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.sysparam.dto.SysParamDto;
import net.ideahut.springboot.task.TaskListExecutor;
import net.ideahut.springboot.task.TaskResult;

@Public
@RestController
@RequestMapping("/kafka")
class KafkaController {

	private final ReplyKafkaSender<String, String, SysParamDto> replySender;
	private final KafkaSender<String, String> stringSender;
	private final KafkaSender<String, byte[]> bytesSender;
	
	@Autowired
	KafkaController(
		KafkaHandler kafkaHandler	
	) {
		this.replySender = kafkaHandler.createReplySender("SAMPLE.REPLY");
		this.stringSender = kafkaHandler.createSender("SAMPLE.STRING");
		this.bytesSender = kafkaHandler.createSender("SAMPLE.BYTES");
	}
	
	@GetMapping("/reply")
	Result reply(
		@RequestParam("text") String text,
		@RequestParam("total") Integer total
	) {
		int threads = total > 100 ? 100 : total;
		TaskListExecutor executor = TaskListExecutor.of(threads);
		for (int i = 0; i < total; i++) {
			int fi = i;
			executor.add(new Callable<SysParamDto>() {
				@Override
				public SysParamDto call() throws Exception {
					return replySender.sendAndReceive(fi + "::" + text).get(10, TimeUnit.SECONDS).value();
				}
			});
		}
		List<TaskResult> data = executor.getResults();
		return Result.success(data).setInfo("text", text).setInfo("total", total);
	}
	
	
	@GetMapping("/send/string")
	Result sendString(
		@RequestParam("text") String text,
		@RequestParam("total") Integer total
	) {
		for (int i = 0; i < total; i++) {
			stringSender.send(text + "::STRING::" + System.nanoTime());
		}
		return Result.success();
	}
	
	
	@GetMapping("/send/bytes")
	Result sendBytes(
		@RequestParam("text") String text,
		@RequestParam("total") Integer total
	) {
		for (int i = 0; i < total; i++) {
			bytesSender.send((text + "::BYTES::" + System.nanoTime()).getBytes());
		}
		return Result.success();
	}
	
}
