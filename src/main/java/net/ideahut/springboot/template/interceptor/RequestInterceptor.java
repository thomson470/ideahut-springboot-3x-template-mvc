package net.ideahut.springboot.template.interceptor;

import java.util.List;
import java.util.Map;

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
import net.ideahut.springboot.admin.WebMvcAdminSecurity;
import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiHandler;
import net.ideahut.springboot.api.WebMvcApiValidator;
import net.ideahut.springboot.audit.AuditInfo;
import net.ideahut.springboot.context.RequestContext;
import net.ideahut.springboot.object.MapStringObject;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.security.SecurityCredential;
import net.ideahut.springboot.security.SecurityUser;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.Application;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.util.FrameworkUtil;
import net.ideahut.springboot.util.WebMvcUtil;

@Component
@ComponentScan
public class RequestInterceptor implements HandlerInterceptor {
	
	@Autowired
	private AppProperties appProperties;
	@Autowired
	private AdminHandler adminHandler;
	@Autowired
	private ApiHandler apiHandler;
	@Qualifier(AppConstants.Bean.Security.ADMIN)
	@Autowired
	private WebMvcAdminSecurity adminSecurity;
	@Qualifier(AppConstants.Bean.Credential.ADMIN)
	@Autowired
	private SecurityCredential adminCredential;
	@Autowired
	private WebMvcApiValidator apiValidator;
	
	// set true jika ingin mengecek berdasarkan RequestPermission
	private boolean isCheckRequestEnabled = false;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)	throws Exception {
		if (!Application.isReady()) {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			return false;
		}
		AuditInfo.context().setAuditor(AppConstants.Profile.SYSTEM);
		
		if (handler instanceof HandlerMethod) {
			HandlerMethod hm = (HandlerMethod) handler;
			if (appProperties.getIgnoredHandlerClasses().contains(hm.getBeanType())) {
				return true;			
			}
			AuditInfo.context().setInfo(hm.getBeanType().getName() + ":" + hm.getMethod().getName() + "(" + hm.getMethod().getParameterCount() + ")");	
			if (adminHandler.isAdminPath(request.getServletPath())) {
				SecurityUser user = adminCredential.getSecurityUser(new MapStringObject().put(SecurityUser.Parameter.AUTHORIZATION, request.getHeader(adminSecurity.getHeaderKey())));
				if (user != null) {
					AuditInfo.context().setAuditor("ADMIN::" + user.getUsername());
				}
			} else {
				Public anPublic = FrameworkUtil.getAnnotation(Public.class, hm);
				boolean isPublic = anPublic != null && anPublic.value();
				ApiAccess access = apiValidator.getApiAccess(request, isPublic);
				if (access == null) {
					access = new ApiAccess();
					access.setRole(AppConstants.Default.API_ROLE);
				}
				if (!isPublic && isCheckRequestEnabled) {
					boolean allowed = apiHandler.isRequestAllowed(access.getRole(), hm);
					if (!allowed) {
						throw FrameworkUtil.exception(Result.error("REQ-00", "Request is not allowed"));
					}
				}
				RequestContext.currentContext().setAttribute(ApiAccess.CONTEXT, access);
			}
		}
		else if (handler instanceof ResourceHttpRequestHandler) {
			if (adminHandler.isAdminPath(request.getServletPath())) {
				Map<String, List<String>> parameters = WebMvcUtil.getRequestParameters(request);
				String redirect = adminHandler.getRedirect(adminCredential, request.getServletPath(), parameters, request.getQueryString());
				if (redirect != null) {
					response.sendRedirect(redirect);
					return false;
				}
			} else {
				// do noting
			}
		}
		return true;
	}	
	
}
