package net.ideahut.springboot.template.interceptor;

import java.io.IOException;
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
import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiUser;
import net.ideahut.springboot.api.WebMvcApiService;
import net.ideahut.springboot.audit.AuditInfo;
import net.ideahut.springboot.context.RequestContext;
import net.ideahut.springboot.exception.ResultRuntimeException;
import net.ideahut.springboot.message.MessageHandler;
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
	
	private final AppProperties appProperties;
	private final AdminHandler adminHandler;
	private final WebMvcAdminSecurity adminSecurity;
	private final SecurityCredential adminCredential;
	private final WebMvcApiService apiService;
	
	// set false, agar ApiService tidak melakukan pengecekan (development)
	// default ApiAccess Role = PUBLIC
	private static final boolean CHECK_BY_API_SERVICE = false;
	// set true, jika tidak ingin mengecek ApiRoleRequest / ApiPropvider request (development)
	private static final boolean ALLOW_ALL_REQUEST = true;
	
	@Autowired
	RequestInterceptor(
		AppProperties appProperties,
		AdminHandler adminHandler,
		@Qualifier(AppConstants.Bean.Security.ADMIN)
		WebMvcAdminSecurity adminSecurity,
		@Qualifier(AppConstants.Bean.Credential.ADMIN)
		SecurityCredential adminCredential,
		WebMvcApiService apiService
	) {
		this.appProperties = appProperties;
		this.adminHandler = adminHandler;
		this.adminSecurity = adminSecurity;
		this.adminCredential = adminCredential;
		this.apiService = apiService;
	}
	
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
				handleAdmin(request);
			} else {
				if (FrameworkUtil.isApiSkip(hm)) {
					return true;
				}
				handleApi(request, hm);
			}
		}
		else if (handler instanceof ResourceHttpRequestHandler) {
			return handleResource(request, response);
		}
		return true;
	}
	
	private void handleAdmin(HttpServletRequest request) {
		RequestContext.currentContext().setAttribute(MessageHandler.Attribute.LANGUAGE, "en");
		SecurityUser user = adminCredential.getSecurityUser(new MapStringObject().setValue(SecurityUser.Parameter.AUTHORIZATION, request.getHeader(adminSecurity.getHeaderKey())));
		if (user != null) {
			AuditInfo.context().setAuditor("ADMIN::" + user.getUsername());
		}
	}
	
	private void handleApi(HttpServletRequest request, HandlerMethod hm) {
		boolean isPublic = FrameworkUtil.isPublic(hm);
		ApiAccess apiAccess = null;
		if (CHECK_BY_API_SERVICE) {
			apiAccess = apiService.getApiAccess(request, isPublic);
			if (apiAccess == null) {
				apiAccess = new ApiAccess();
				apiAccess.setApiRole(AppConstants.Default.API_ROLE);
			}
			if (!ALLOW_ALL_REQUEST && !isPublic) {
				boolean allowed = apiService.isApiRequestAllowed(apiAccess, hm);
				if (!allowed) {
					throw ResultRuntimeException.of(Result.error("REQ-00", "Request is not allowed"));
				}
			}
		} else {
			apiAccess = new ApiAccess();
			apiAccess.setApiRole(AppConstants.Default.API_ROLE);
		}
		String auditor = "";
		if (Boolean.TRUE.equals(apiAccess.getIsConsumer())) {
			auditor += "CONSUMER::" + apiAccess.getConsumerId();
		}
		auditor += "ROLE::" + apiAccess.getApiRole();
		ApiUser apiUser = apiAccess.getApiUser();
		if (apiUser != null) {
			auditor += "USER::" + apiUser.getId() + "::" + apiUser.getUsername();
		}
		AuditInfo.context().setAuditor(auditor);
		RequestContext.currentContext().setAttribute(ApiAccess.CONTEXT, apiAccess);
	}
	
	private boolean handleResource(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (adminHandler.isAdminPath(request.getServletPath())) {
			Map<String, List<String>> parameters = WebMvcUtil.getRequestParameters(request);
			String redirect = adminHandler.getRedirect(adminCredential, request.getServletPath(), parameters, request.getQueryString());
			if (redirect != null) {
				response.sendRedirect(redirect);
				return false;
			}
		}
		return true;
	}	
	
}
