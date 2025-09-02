package net.ideahut.springboot.template.entity.app;

import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAuditSoftDelete;

@Audit
@Entity
@Table(name = "role")
@Setter
@Getter
@SuppressWarnings("serial")
public class Role extends EntityAuditSoftDelete {

	@Id
	@Column(name = "role_code", unique = true, nullable = false, length = 64)
	private String roleCode;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "is_admin", nullable = false, length = 1)
	private Character isAdmin;
	
	@Column(name = "is_active", nullable = false, length = 1)
	private Character isActive;
	
	@Transient
	private Map<String, List<Menu>> menus;
	
	public Role() {}
	
	public Role(String roleCode) {
		this.roleCode = roleCode;
	}

}
