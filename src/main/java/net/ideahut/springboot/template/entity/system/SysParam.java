package net.ideahut.springboot.template.entity.system;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.ApiExclude;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.sysparam.entity.EntSysParam;
import net.ideahut.springboot.sysparam.entity.EntSysParamId;

@ApiExclude
@Audit
@Entity
@Table(name = "sysparam")
@Setter
@Getter
@SuppressWarnings("serial")
public class SysParam extends EntSysParam {

	public SysParam() {
		super();
	}

	public SysParam(EntSysParamId id) {
		super(id);
	}
	
}
