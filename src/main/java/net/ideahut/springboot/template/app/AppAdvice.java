package net.ideahut.springboot.template.app;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.bean.BeanConfigure;
import net.ideahut.springboot.context.RequestContext;
import net.ideahut.springboot.exception.ResultRuntimeException;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.properties.AppProperties;

@Slf4j
@ControllerAdvice
class AppAdvice implements  ResponseBodyAdvice<Object>, BeanConfigure<AppAdvice> {
	
	private Set<String> handleAllExceptionSkipPaths = new LinkedHashSet<>();
	private Set<String> beforeBodyWriteSkipPaths = new LinkedHashSet<>();
	private boolean isLogAllError = true;
	
	@Override
	public Callable<AppAdvice> onConfigureBean(ApplicationContext applicationContext) {
		AppAdvice self = this;
		return () -> {
			AdminHandler adminHandler = applicationContext.getBean(AdminHandler.class);
			AppProperties appProperties = applicationContext.getBean(AppProperties.class);
			isLogAllError = Boolean.TRUE.equals(appProperties.getLogAllError());
			handleAllExceptionSkipPaths.add(adminHandler.getWebPath());
			String actuatorBasePath = FrameworkHelper.getActuatorBasePath(applicationContext);
			ObjectHelper.runIf(actuatorBasePath != null, () -> beforeBodyWriteSkipPaths.add(actuatorBasePath));
			return self;
		};
	}
	
	@ExceptionHandler
    @ResponseStatus(code = HttpStatus.OK)
    @ResponseBody
    public Result handleAllException(
    	HttpServletRequest request,
    	Throwable throwable
    ) {
		String path = request.getServletPath();
		boolean skip = isSkipPath(handleAllExceptionSkipPaths, path);
		return ObjectHelper.callIf(
			!skip, 
			() -> {
				ObjectHelper.runIf(isLogAllError, () -> log.error(AppAdvice.class.getSimpleName(), throwable));
				return ResultRuntimeException.of(throwable).getResult();
			}
		);
    }

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(
		Object body, 
		MethodParameter returnType, 
		MediaType selectedContentType,
		Class<? extends HttpMessageConverter<?>> selectedConverterType, 
		ServerHttpRequest request,
		ServerHttpResponse response
	) {
		String path = ((ServletServerHttpRequest) request).getServletRequest().getServletPath();
		boolean skip = isSkipPath(beforeBodyWriteSkipPaths, path);
		return ObjectHelper.callOrElse(
			skip, 
			() -> body, 
			() ->
				ObjectHelper.callOrElse(
					ObjectHelper.isInstance(byte[].class, body), 
					() -> {
						RequestContext.destroy();
						try {
							byte[] bytes = (byte[]) body;
							response.getBody().write(bytes);
						} catch (Exception e) {
							throw ResultRuntimeException.of(e);
						}
						return null;
					}, 
					() -> {
						Object nobj = ObjectHelper.callOrElse(
							ObjectHelper.isInstance(Result.class, body), 
							() -> {
								((Result) body).updateTime();
								return body;
							}, 
							() -> Result.success(body)
						);
						RequestContext.destroy();
						return nobj;
					}
				)
		);
	}
	
	private boolean isSkipPath(Set<String> skipPaths, String path) {
		for (String skipPath : skipPaths) {
			if (path.startsWith(skipPath)) {
				return true;
			}
		}
		return false;
	}
    
}
