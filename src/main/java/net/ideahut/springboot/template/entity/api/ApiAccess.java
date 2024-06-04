package net.ideahut.springboot.template.entity.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.ApiExclude;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.api.entity.EntAccess;
import net.ideahut.springboot.api.entity.EntAccessId;

@ApiExclude
@Audit
@Entity
@Table(name = "api_access")
@Setter
@Getter
@SuppressWarnings("serial")
public class ApiAccess extends EntAccess {

	public ApiAccess() {
		super();
	}

	public ApiAccess(EntAccessId id) {
		super(id);
	}
	
}
