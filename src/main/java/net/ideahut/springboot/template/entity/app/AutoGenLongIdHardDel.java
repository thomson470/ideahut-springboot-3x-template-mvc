package net.ideahut.springboot.template.entity.app;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAudit;

@Audit
@Entity
@Table(name = "auto_gen_long_id_hard_del")
@Setter
@Getter
@SuppressWarnings("serial")
public class AutoGenLongIdHardDel extends EntityAudit {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	
	@Column(name = "name", nullable = false, length = 128)
	private String name;
	
	@Column(name = "description")
	private String description;

	@Column(name = "is_active", nullable = false, length = 1)
	private Character isActive;
	
	public AutoGenLongIdHardDel() {}
	
	public AutoGenLongIdHardDel(Long id) {
		this.id = id;
	}
	
}
