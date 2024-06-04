package net.ideahut.springboot.template.entity.api;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.ApiExclude;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.api.entity.EntConfig;
import net.ideahut.springboot.api.entity.EntConfigId;

@ApiExclude
@Audit
@Entity
@Table(name = "api_access_config")
@Setter
@Getter
@SuppressWarnings("serial")
public class ApiAccessConfig extends EntConfig {

	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns(
		value = {
			@JoinColumn(name = "source", referencedColumnName = "source", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "code", referencedColumnName = "code", nullable = false, insertable = false, updatable = false)
		},
		foreignKey = @ForeignKey(name = "fk_api_access_config__access")
	)
	private ApiAccess access;

	public ApiAccessConfig() {
		super();
	}

	public ApiAccessConfig(EntConfigId id) {
		super(id);
	}
	
}
