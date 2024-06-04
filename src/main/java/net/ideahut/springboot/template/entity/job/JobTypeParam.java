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
import net.ideahut.springboot.job.entity.EntTypeParam;
import net.ideahut.springboot.job.entity.EntTypeParamId;

@ApiExclude
@Audit
@Entity
@Table(name = "job_type_param")
@Setter
@Getter
@SuppressWarnings("serial")
public class JobTypeParam extends EntTypeParam {

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
	
	public JobTypeParam() {
		super();
	}

	public JobTypeParam(EntTypeParamId id) {
		super(id);
	}
	
}
