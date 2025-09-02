package net.ideahut.springboot.template.entity.app;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAuditSoftDelete;
import net.ideahut.springboot.generator.OdtIdGenerator;

@Audit
@Entity
@Table(name = "auto_gen_str_id_soft_del")
@Setter
@Getter
@SuppressWarnings("serial")
public class AutoGenStrIdSoftDel extends EntityAuditSoftDelete {
	
	@Id
	@GeneratedValue(generator = OdtIdGenerator.NAME)
	@GenericGenerator(name = OdtIdGenerator.NAME, type = OdtIdGenerator.class)
	@Column(name = "id", unique = true, nullable = false, length = 64)
	private String id;
	
	@Column(name = "name", nullable = false, length = 128)
	private String name;
	
	@Column(name = "description")
	private String description;

	@Column(name = "is_active", nullable = false, length = 1)
	private Character isActive;
	
	public AutoGenStrIdSoftDel() {}
	
	public AutoGenStrIdSoftDel(String id) {
		this.id = id;
	}
	
}
