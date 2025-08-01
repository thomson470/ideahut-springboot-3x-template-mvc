package net.ideahut.springboot.template.config;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints({RuntimeHintsConfig.Registrar.class})
class RuntimeHintsConfig {
	
	RuntimeHintsConfig() {}

	static class Registrar implements RuntimeHintsRegistrar {

		@Override
		public void registerHints(RuntimeHints hints, ClassLoader loader) {
			/*
			 * Tambah class-class yang akan diregistrasi untuk native di sini
			 */
			
		}
		
	}
	
}
