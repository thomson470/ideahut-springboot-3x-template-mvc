package net.ideahut.springboot.template.listener.entity;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import net.ideahut.springboot.entity.EntityPreListener;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.app.AppConstants;

@Component
@ComponentScan
class AppEntityPreListener implements EntityPreListener, InitializingBean {
	
	private final TaskHandler taskHandler;
	
	private Map<Class<?>, EntityPreListener> listeners = new HashMap<>();
	
	@Autowired
	AppEntityPreListener(
		@Qualifier(AppConstants.Bean.Task.AUDIT)
		TaskHandler taskHandler
	) {
		this.taskHandler = taskHandler;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		listeners.clear();
	}
	
	@Override
	public void onPreInsert(Object entity) {
		taskHandler.execute(() -> {
			EntityPreListener listener = listeners.get(entity.getClass());
			ObjectHelper.runIf(listener != null, () -> listener.onPreInsert(entity));
		});
	}
	
	@Override
	public void onPreUpdate(Object entity) {
		taskHandler.execute(() -> {
			EntityPreListener listener = listeners.get(entity.getClass());
			ObjectHelper.runIf(listener != null, () -> listener.onPreUpdate(entity));
		});
	}

	@Override
	public void onPreDelete(Object entity) {
		taskHandler.execute(() -> {
			EntityPreListener listener = listeners.get(entity.getClass());
			ObjectHelper.runIf(listener != null, () -> listener.onPreDelete(entity));
		});
	}
	
}
