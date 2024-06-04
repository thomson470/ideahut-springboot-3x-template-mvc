package net.ideahut.springboot.template.entity.job;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.ApiExclude;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.job.entity.EntTrigger;

@ApiExclude
@Audit
@Entity
@Table(name = "job_trigger")
@Setter
@Getter
@SuppressWarnings("serial")
public class JobTrigger extends EntTrigger {
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "group_id", 
		nullable = false, 
		insertable = false,
		updatable = false,
		foreignKey = @ForeignKey(name = "fk_job_trigger__group")
	)
	private JobGroup jobGroup;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "type_id", 
		nullable = false, 
		insertable = false,
		updatable = false,
		foreignKey = @ForeignKey(name = "fk_job_trigger__type")
	)
	private JobType jobType;
	
	@OnDelete(action = OnDeleteAction.SET_NULL)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "instance_id",
		insertable = false,
		updatable = false,
		foreignKey = @ForeignKey(name = "fk_job_trigger__instance")
	)
	private JobInstance jobInstance;
	

	public JobTrigger() {
		super();
	}

	public JobTrigger(String triggerId) {
		super(triggerId);
	}
	
}
