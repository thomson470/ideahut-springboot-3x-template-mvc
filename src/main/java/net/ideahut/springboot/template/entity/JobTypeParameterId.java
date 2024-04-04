package net.ideahut.springboot.template.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter @Getter
@SuppressWarnings("serial")
public class JobTypeParameterId implements java.io.Serializable {

	@Column(name = "type_id", nullable = false, length = 64)
	private String typeId;
	
	@Column(name = "parameter_name", nullable = false, length = 100)
	private String parameterName;
	
	public JobTypeParameterId() {}
	
	public JobTypeParameterId(String typeId, String parameterName) {
		this.typeId = typeId;
		this.parameterName = parameterName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(parameterName, typeId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobTypeParameterId other = (JobTypeParameterId) obj;
		return Objects.equals(parameterName, other.parameterName) && Objects.equals(typeId, other.typeId);
	}
	
}
