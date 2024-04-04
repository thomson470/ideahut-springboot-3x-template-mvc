package net.ideahut.springboot.template.entity;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAudit;
import net.ideahut.springboot.generator.HbmStringGenerator;

@Audit
@Entity
@Table(name = "job_group")
@Setter
@Getter
@SuppressWarnings("serial")
public class JobGroup extends EntityAudit {

	@Id
	@GeneratedValue(generator = HbmStringGenerator.NAME)
	@GenericGenerator(name = HbmStringGenerator.NAME, strategy = HbmStringGenerator.STRATEGY)
	@Column(name = "group_id", unique = true, nullable = false, length = 64)
	private String groupId;
	
	@Column(name = "name", nullable = false, length = 100)
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "is_active", nullable = false, length = 1)
	private Character isActive;
	
	@Column(name = "zone_offset_seconds", nullable = false)
	private Integer zoneOffsetSeconds;
	
	@Transient
	private List<JobTrigger> triggers;
	
	public JobGroup() {}
	
	public JobGroup(String groupId) {
		this.groupId = groupId;
	}
	
}
