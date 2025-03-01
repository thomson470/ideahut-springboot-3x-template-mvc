package net.ideahut.springboot.template.entity.app;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAudit;

@Audit
@Entity
@Table(name = "embedded_without_attribute")
@Setter
@Getter
@SuppressWarnings("serial")
public class EmbeddedWithoutAttribute extends EntityAudit {
	
	@EmbeddedId
	private EmbededId id;
	
	@Column(name = "name", nullable = false, length = 128)
	private String name;
	
	@Column(name = "description")
	private String description;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;
	
	public EmbeddedWithoutAttribute() {}
	
	public EmbeddedWithoutAttribute(EmbededId id) {
		this.id = id;
	}
	
}
