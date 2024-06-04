package net.ideahut.springboot.template.entity.api;

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
import net.ideahut.springboot.api.entity.EntRequestRole;
import net.ideahut.springboot.api.entity.EntRequestRoleId;

@ApiExclude
@Audit
@Entity
@Table(name = "api_request_role")
@Setter
@Getter
@SuppressWarnings("serial")
public class ApiRequestRole extends EntRequestRole {
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "role_code", 
		nullable = false, 
		insertable = false, 
		updatable = false, 
		foreignKey = @ForeignKey(name = "fk_api_request_role__role")
	)
	private ApiRole role;

	public ApiRequestRole() {
		super();
	}

	public ApiRequestRole(EntRequestRoleId id) {
		super(id);
	}	
	
}
