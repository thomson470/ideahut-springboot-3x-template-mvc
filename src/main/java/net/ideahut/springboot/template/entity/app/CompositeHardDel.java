package net.ideahut.springboot.template.entity.app;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAudit;

@Audit
@Entity
@Table(name = "composite_hard_del")
@Setter
@Getter
@SuppressWarnings("serial")
public class CompositeHardDel extends EntityAudit {
	
	@Id
	@Column(name = "type", nullable = false)
	private Integer type;
	
	@Id
	@Column(name = "code", length = 3, nullable = false)
	private String code;
	
	@Column(name = "name", nullable = false, length = 128)
	private String name;
	
	@Column(name = "description")
	private String description;

	@Column(name = "is_active", nullable = false, length = 1)
	private Character isActive;
	
	public CompositeHardDel() {}
	
	public CompositeHardDel(Integer type, String code) {
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
		CompositeHardDel other = (CompositeHardDel) obj;
		return Objects.equals(code, other.code) && Objects.equals(type, other.type);
	}
	
}
