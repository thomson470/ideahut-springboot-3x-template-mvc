package net.ideahut.springboot.template.entity;

import java.sql.Types;

import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAudit;

@Audit
@Entity
@Table(name = "sysparam")
@Setter
@Getter
@SuppressWarnings("serial")
public class SysParam extends EntityAudit {
	
	@EmbeddedId
	@AttributeOverride(name = "sysCode", column = @Column(name = "sys_code", nullable = false))
	@AttributeOverride(name = "paramCode", column = @Column(name = "param_code", nullable = false)) 
	private SysParamId id;
	
	@Column(name = "description")
	private String description;
	
	@JdbcTypeCode( Types.LONGVARCHAR )
	@Column(name = "value")
	private String value;
	
	@Lob
	@Column(name = "bytes")
	private byte[] bytes;
	
	public SysParam() {}
	
	public SysParam(SysParamId id) {
		this.id = id;
	}
	
}
