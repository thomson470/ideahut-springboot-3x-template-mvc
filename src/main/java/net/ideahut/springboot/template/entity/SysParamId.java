package net.ideahut.springboot.template.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter
@Getter
@SuppressWarnings("serial")
public class SysParamId implements java.io.Serializable {
	
	@Column(name = "sys_code", nullable = false)
	private String sysCode;
	
	@Column(name = "param_code", nullable = false)
	private String paramCode;
	
	public SysParamId() {}
	
	public SysParamId(String sysCode, String paramCode) {
		this.sysCode = sysCode;
		this.paramCode = paramCode;
	}

	@Override
	public int hashCode() {
		return Objects.hash(paramCode, sysCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SysParamId other = (SysParamId) obj;
		return Objects.equals(paramCode, other.paramCode) && Objects.equals(sysCode, other.sysCode);
	}	
	
}
