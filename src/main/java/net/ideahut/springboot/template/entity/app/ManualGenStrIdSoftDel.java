package net.ideahut.springboot.template.entity.app;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAuditSoftDelete;

@Audit
@Entity
@Table(name = "manual_gen_str_id_soft_del")
@Setter
@Getter
@SuppressWarnings("serial")
public class ManualGenStrIdSoftDel extends EntityAuditSoftDelete {
	
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 64)
	private String id;
	
	@Column(name = "name", nullable = false, length = 128)
	private String name;
	
	@Column(name = "description")
	private String description;

	@Column(name = "is_active", nullable = false, length = 1)
	private Character isActive;
	
	public ManualGenStrIdSoftDel() {}
	
	public ManualGenStrIdSoftDel(String id) {
		this.id = id;
	}
	
}
