package net.ideahut.springboot.template;

import org.hibernate.Version;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.SpringVersion;

import lombok.extern.slf4j.Slf4j;
import net.ideahut.springboot.IdeahutVersion;
import net.ideahut.springboot.audit.AuditHandler;
import net.ideahut.springboot.bean.BeanConfigure;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.init.InitHandler;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.util.FrameworkUtil;

/*
 * 1. Main Class, untuk eksekusi aplikasi.
 *    - Method: main()
 * 
 * 2. Bean-bean dapat direconfigure sesuai priority-nya.
 *    - Method: onApplicationEvent() -> implements ApplicationListener<ContextRefreshedEvent>
 *    
 */

@Slf4j
@SpringBootApplication
public class Application extends SpringBootServletInitializer implements ApplicationListener<ContextRefreshedEvent> {
	
	private static boolean ready = false;
	
	public static boolean isReady() {
		return ready;
	}
	
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Application.class);
		application.setLazyInitialization(false);
		application.setLogStartupInfo(true);
		application.run(args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ready = false;
		ApplicationContext applicationContext = event.getApplicationContext();
		FrameworkUtil.checkDependecies(applicationContext);
		
		log.info("**** Initializing application '" + applicationContext.getId() + "'");
		
		TaskHandler taskHandler = applicationContext.getBean(AppConstants.Bean.Task.COMMON, TaskHandler.class);
		taskHandler.execute(() -> {
			try {
				BeanConfigure.runBeanConfigure(
					taskHandler, 
					applicationContext, 
					EntityTrxManager.class, 
					AuditHandler.class
				);
			} catch (Exception e) {
				throw FrameworkUtil.exception(e);
			}
			InitHandler initHandler = applicationContext.getBean(InitHandler.class);
			taskHandler.execute(() -> {
				try {
					initHandler.initClass();
					initHandler.initMapper(applicationContext);
					initHandler.initValidation();
				} catch (Exception e) {
					throw FrameworkUtil.exception(e);
				}
			});
			
			/*
			SchedulerHandler schedulerHandler = applicationContext.getBean(SchedulerHandler.class);
			taskHandler.execute(() -> {
				try {
					schedulerHandler.start();
				} catch (Exception e) {
					log.error("SchedulerHandler", e);
				}
			});
			*/
			
			ready = true;
			
			// Inisialisasi Servlet dengan mengirim request ke endpoint /warmup (Lihat WarmUpController)
			// Request dikirim setelah reconfigure selesai
			initHandler.initServlet();
			
			log.info("**** Reactive         : " + FrameworkUtil.isReactiveApplication(applicationContext));
			log.info("**** JDK              : " + System.getProperty("java.version"));
			log.info("**** Spring Framework : " + SpringVersion.getVersion());
			log.info("**** Spring Boot      : " + SpringBootVersion.getVersion());
			log.info("**** Hibernate        : " + Version.getVersionString());
			log.info("**** Ideahut          : " + IdeahutVersion.getVersion());
			log.info("**** Application '" + applicationContext.getId() + "' is ready to serve on port " + FrameworkUtil.getPort(applicationContext));
			
		});
	}
	
}
