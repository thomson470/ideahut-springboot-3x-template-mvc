package net.ideahut.springboot.template.listener;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@ComponentScan
public class EntityPreListener implements net.ideahut.springboot.entity.EntityPreListener {
	
	@Override
	public void onPreDelete(Object entity) {
		updateCache(entity, false);
	}

	@Override
	public void onPreUpdate(Object entity) {
		updateCache(entity, false);
	}

	@Override
	public void onPreInsert(Object entity) {
		updateCache(entity, true);
	}
	
	private void updateCache(Object entity, boolean isInsert) {
		
		
	}
	
}
