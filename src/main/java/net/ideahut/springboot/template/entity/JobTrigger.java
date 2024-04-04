package net.ideahut.springboot.template.entity;

import java.util.Map;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAudit;
import net.ideahut.springboot.generator.HbmStringGenerator;

@Audit
@Entity
@Table(name = "job_trigger")
@Setter
@Getter
@SuppressWarnings("serial")
public class JobTrigger extends EntityAudit {

	@Id
	@GeneratedValue(generator = HbmStringGenerator.NAME)
	@GenericGenerator(name = HbmStringGenerator.NAME, strategy = HbmStringGenerator.STRATEGY)
	@Column(name = "trigger_id", unique = true, nullable = false, length = 64)
	private String triggerId;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "group_id", 
		nullable = false, 
		foreignKey = @ForeignKey(name = "fk_job_trigger__group")
	)
	private JobGroup group;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "type_id", 
		nullable = false, 
		foreignKey = @ForeignKey(name = "fk_job_trigger__type")
	)
	private JobType type;
	
	@Column(name = "name", nullable = false, length = 100)
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "cron_expression", nullable = false, length = 100)
	private String cronExpression;
	
	@Column(name = "zone_offset_seconds")
	private Integer zoneOffsetSeconds;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "instance_id", 
		foreignKey = @ForeignKey(name = "fk_job_trigger__instance")
	)
	private JobInstance instance;
	
	@Column(name = "is_run_on_startup", nullable = false, length = 1)
	private Character isRunOnStartup;
	
	@Column(name = "is_active", nullable = false, length = 1)
	private Character isActive;
	
	@Column(name = "is_save_result", nullable = false, length = 1)
	private Character isSaveResult;
	
	@Column(name = "last_run_time")
	private Long lastRunTime;
	
	@Lob
	@Column(name = "last_run_data")
	private String lastRunData;
	
	@Transient
	private Map<String, JobTriggerConfiguration> configurations;
	
	public JobTrigger() {}
	
	public JobTrigger(String triggerId) {
		this.triggerId = triggerId;
	}
	
}
