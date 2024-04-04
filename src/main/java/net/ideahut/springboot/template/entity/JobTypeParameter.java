package net.ideahut.springboot.template.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAudit;

@Audit
@Entity
@Table(name = "job_type_parameter")
@Setter
@Getter
@SuppressWarnings("serial")
public class JobTypeParameter extends EntityAudit {

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "typeId", column = @Column(name = "type_id", nullable = false, length = 64)),
		@AttributeOverride(name = "parameterName", column = @Column(name = "parameter_name", nullable = false, length = 100)) 
	})
	private JobTypeParameterId id;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "type_id", 
		nullable = false, 
		insertable = false, 
		updatable = false, 
		foreignKey = @ForeignKey(name = "fk_job_type_parameter__type")
	)
	private JobType type;
	
	@Column(name = "description")
	private String description;
	
}
