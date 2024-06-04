package net.ideahut.springboot.template.entity.job;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.ApiExclude;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.job.entity.EntInstance;

@ApiExclude
@Audit
@Entity
@Table(name = "job_instance")
@Setter
@Getter
@SuppressWarnings("serial")
public class JobInstance extends EntInstance {

	public JobInstance() {
		super();
	}

	public JobInstance(String instanceId) {
		super(instanceId);
	}	
	
}
