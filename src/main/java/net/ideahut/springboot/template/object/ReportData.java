package net.ideahut.springboot.template.object;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportData implements Serializable {
	private static final long serialVersionUID = 7584285995649716755L;
	
	private String uuid;
	private String name;
	private Long number;
	private String description;
	
}
