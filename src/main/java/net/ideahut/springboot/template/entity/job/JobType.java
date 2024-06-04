package net.ideahut.springboot.template.entity.job;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.ApiExclude;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.job.entity.EntType;

@ApiExclude
@Audit
@Entity
@Table(
	name = "job_type", 
	uniqueConstraints = @UniqueConstraint(
		columnNames = {"classname"}, 
		name = "uq_job_type__classname"
	)
)
@Setter
@Getter
@SuppressWarnings("serial")
public class JobType extends EntType {

	public JobType() {
		super();
	}

	public JobType(String typeId) {
		super(typeId);
	}	
	
}
