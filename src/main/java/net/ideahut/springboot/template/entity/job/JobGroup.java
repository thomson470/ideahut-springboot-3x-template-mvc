package net.ideahut.springboot.template.entity.job;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.ApiExclude;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.job.entity.EntGroup;

@ApiExclude
@Audit
@Entity
@Table(name = "job_group")
@Setter
@Getter
@SuppressWarnings("serial")
public class JobGroup extends EntGroup {

	public JobGroup() {
		super();
	}

	public JobGroup(String groupId) {
		super(groupId);
	}	
	
}
