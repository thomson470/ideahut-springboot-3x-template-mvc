package net.ideahut.springboot.template.object;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CacheData implements Serializable {
	private static final long serialVersionUID = -1166677166998269733L;
	
	private String group;
	private String key;
	private String content;
	private Long timestamp;
	
}
