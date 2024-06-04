package net.ideahut.springboot.template.listener;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import net.ideahut.springboot.entity.EntityPreListener;
import net.ideahut.springboot.task.TaskHandler;

@Component
@ComponentScan
class AppEntityPreListener implements EntityPreListener, InitializingBean {
	
	private final TaskHandler taskHandler;
	
	private Map<Class<?>, EntityPreListener> entities = new HashMap<>();
	
	AppEntityPreListener(
		TaskHandler taskHandler
	) {
		this.taskHandler = taskHandler;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		entities.clear();
	}
	
	@Override
	public void onPreDelete(Object entity) {
		taskHandler.execute(() -> {
			EntityPreListener listener = entities.get(entity.getClass());
			if (listener != null) {
				listener.onPreDelete(entity);
			}
		});
	}

	@Override
	public void onPreUpdate(Object entity) {
		taskHandler.execute(() -> {
			EntityPreListener listener = entities.get(entity.getClass());
			if (listener != null) {
				listener.onPreUpdate(entity);
			}
		});
	}

	@Override
	public void onPreInsert(Object entity) {
		taskHandler.execute(() -> {
			EntityPreListener listener = entities.get(entity.getClass());
			if (listener != null) {
				listener.onPreInsert(entity);
			}
		});
	}
	
}
