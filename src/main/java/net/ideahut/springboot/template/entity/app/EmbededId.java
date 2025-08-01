package net.ideahut.springboot.template.entity.app;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter 
@Getter
@SuppressWarnings("serial")
public class EmbededId implements java.io.Serializable {
	
	@Column(name = "type", nullable = false)
	private Integer type;
	
	@Column(name = "code", length = 3, nullable = false)
	private String code;
	
	public EmbededId() {}
	
	public EmbededId(Integer type, String code) {
		this.type = type;
		this.code = code;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmbededId other = (EmbededId) obj;
		return Objects.equals(code, other.code) && Objects.equals(type, other.type);
	}
	
}
