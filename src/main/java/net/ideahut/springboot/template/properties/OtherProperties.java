package net.ideahut.springboot.template.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.definition.DatabaseAuditDefinition;
import net.ideahut.springboot.entity.DatabaseProperties;

@Configuration
@ConfigurationProperties(prefix = "other")
@Setter
@Getter
public class OtherProperties {
	
	private TrxManager trxManager = new TrxManager();
	
	@Setter
	@Getter
	public static class TrxManager {
		private TrxManagerCfg second = new TrxManagerCfg();
	}
	
	@Setter
	@Getter
	public static class TrxManagerCfg extends DatabaseProperties {
		private static final long serialVersionUID = -3126837702103419771L;
		private DatabaseAuditDefinition audit = new DatabaseAuditDefinition();
	}
	
}
