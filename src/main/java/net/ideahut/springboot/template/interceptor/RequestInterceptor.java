package net.ideahut.springboot.template.interceptor;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.admin.AdminSecurity;
import net.ideahut.springboot.audit.AuditInfo;
import net.ideahut.springboot.object.MapStringObject;
import net.ideahut.springboot.security.SecurityCredential;
import net.ideahut.springboot.security.SecurityUser;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.Application;
import net.ideahut.springboot.template.properties.AppProperties;

@Component
@ComponentScan
public class RequestInterceptor implements HandlerInterceptor {
	
	@Autowired
	private AppProperties appProperties;
	@Autowired
	private AdminHandler adminHandler;
	@Qualifier(AppConstants.Bean.Security.ADMIN)
	@Autowired
	private AdminSecurity adminSecurity;
	@Qualifier(AppConstants.Bean.Credential.ADMIN)
	@Autowired
	private SecurityCredential adminCredential;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)	throws Exception {
		if (!Application.isReady()) {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			return false;
		}
		AuditInfo.context().setAuditor(AppConstants.Profile.SYSTEM);
		
		if (handler instanceof HandlerMethod) {
			Method method = ((HandlerMethod) handler).getMethod();
			if (!appProperties.getIgnoredHandlerClasses().contains(method.getDeclaringClass())) {
				AuditInfo.context().setInfo(method.getDeclaringClass().getSimpleName() + ":" + method.getName());				
			}
			if (adminHandler.isAdminPath(request.getServletPath())) {
				SecurityUser user = adminCredential.getSecurityUser(new MapStringObject().put(SecurityUser.Parameter.AUTHORIZATION, request.getHeader(adminSecurity.getHeaderKey())));
				if (user != null) {
					AuditInfo.context().setAuditor("ADMIN::" + user.getUsername());
				}
			}
		}
		else if (handler instanceof ResourceHttpRequestHandler) {
			String redirect = adminHandler.getRedirect(request.getServletPath(), request.getQueryString());
			if (redirect != null) {
				response.sendRedirect(redirect);
				return false;
			}
		}
		return true;
	}	
	
}
