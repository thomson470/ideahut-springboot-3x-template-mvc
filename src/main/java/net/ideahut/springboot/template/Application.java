package net.ideahut.springboot.template;

import java.io.Closeable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import net.ideahut.springboot.audit.AuditHandler;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.launcher.ApplicationLauncher;
import net.ideahut.springboot.launcher.WebMvcLauncher;
import net.ideahut.springboot.sysparam.SysParamHandler;
import net.ideahut.springboot.task.TaskHandler;

/*
 * 1. Main Class, untuk eksekusi aplikasi.
 *    - Method: main()
 * 
 * 2. Bean-bean dapat direconfigure sesuai priority-nya.
 *    - Method: initialBeanConfigureTypes()
 *    
 */

@SpringBootApplication
public class Application extends WebMvcLauncher {
	
	public static class Package {
		private Package() {}
		public static final String LIBRARY		= FrameworkHelper.PACKAGE;
		public static final String APPLICATION	= "net.ideahut.springboot.template";
	}
	
	private static Closeable runningApp;
	private static boolean ready = false;
	private static void setReady(boolean b) { ready = b; }
	public static boolean isReady() { return ready; }
	
	private final TaskHandler taskHandler;
	
	@Autowired
	Application(
		TaskHandler taskHandler
	) {
		this.taskHandler = taskHandler;
	}
	
	@Override
	public Closeable runningApp() {
		return runningApp;
	}

	@Override
	public TaskHandler taskHandler() {
		return taskHandler;
	}
	
	@Override
	protected Class<? extends ApplicationLauncher> source() {
		return Application.class;
	}

	@Override
	public Class<?>[] initialBeanConfigureTypes() {
		return new Class<?>[] {
			EntityTrxManager.class,
			SysParamHandler.class,
			AuditHandler.class // <-- tidak async karena akan dibaca oleh AdminHandler
		};
	}

	@Override
	public void onReady(ApplicationContext applicationContext) {
		setReady(true);
		super.runInit(applicationContext, true);
	}
	
	
	/*
	 * MAIN
	 */
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Application.class);
		application.setLazyInitialization(false);
		application.setLogStartupInfo(true);
		runningApp = application.run(args);
	}
	
}
