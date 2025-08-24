package net.ideahut.springboot.template.entity.app;

import jakarta.persistence.AttributeOverride;
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
@Table(name = "embedded_hard_del")
@Setter
@Getter
@SuppressWarnings("serial")
public class EmbeddedHardDel extends EntityAudit {
	
	@EmbeddedId
	@AttributeOverride(name = "type", column = @Column(name = "type", nullable = false))
	@AttributeOverride(name = "code", column = @Column(name = "code", nullable = false))
	private EmbededId id;
	
	@Column(name = "name", nullable = false, length = 128)
	private String name;
	
	@Column(name = "description")
	private String description;

	@Column(name = "is_active", nullable = false, length = 1)
	private Character isActive;
	
	public EmbeddedHardDel() {}
	
	public EmbeddedHardDel(EmbededId id) {
		this.id = id;
	}
	
}
