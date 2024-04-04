package net.ideahut.springboot.template.object;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CacheData {

	private String group;
	private String key;
	private String content;
	private Long timestamp;
	
}
