package net.ideahut.springboot.template.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAudit;
import net.ideahut.springboot.util.StringUtil;

@Audit
@Entity
@Table(name = "job_trigger_configuration")
@Setter
@Getter
@SuppressWarnings("serial")
public class JobTriggerConfiguration extends EntityAudit {

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "triggerId", column = @Column(name = "trigger_id", nullable = false, length = 64)),
		@AttributeOverride(name = "typeId", column = @Column(name = "type_id", nullable = false, length = 64)),
		@AttributeOverride(name = "parameterName", column = @Column(name = "parameter_name", nullable = false, length = 100)) 
	})
	private JobTriggerConfigurationId id;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "trigger_id", 
		nullable = false, 
		insertable = false, 
		updatable = false, 
		foreignKey = @ForeignKey(name = "fk_job_trigger_configuration__trigger")
	)
	private JobTrigger jobTrigger;
	
	//@OnDelete(action = OnDeleteAction.CASCADE)
	//@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "type_id", nullable = false, insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_job_trigger_configuration__type"))
	//private JobType type;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns(
		value = {
			@JoinColumn(name = "type_id", referencedColumnName = "type_id", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "parameter_name", referencedColumnName = "parameter_name", nullable = false, insertable = false, updatable = false)
		},
		foreignKey = @ForeignKey(name = "fk_job_trigger_configuration__type_parameter")
	)
	private JobTypeParameter typeParameter;
	
	@Lob
	@Column(name = "value")
	private String value;
	
	@Lob
	@Column(name = "bytes")
	private byte[] bytes;
	
	@Transient
	private String base64Bytes;
	@Transient
	private Boolean hasBytes;
	
	public JobTriggerConfiguration() {}
	
	public JobTriggerConfiguration(JobTriggerConfigurationId id) {
		this.id = id;
	}
	
	public JobTriggerConfiguration(JobTriggerConfigurationId id, String value, byte[] bytes) {
		this.id = id;
		this.value = value;
		this.bytes = bytes;
	}
	
	@Transient
	@JsonIgnore
	public <T> T getValue(Class<T> type, T defaultValue) {
		return StringUtil.valueOf(type, value, defaultValue);
	}
	
	@Transient
	@JsonIgnore
	public <T> T getValue(Class<T> type) {
		return StringUtil.valueOf(type, value);
	}
	
}
