package net.ideahut.springboot.template.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiRequest;
import net.ideahut.springboot.api.WebMvcApiService;
import net.ideahut.springboot.audit.AuditInfo;
import net.ideahut.springboot.context.RequestContext;
import net.ideahut.springboot.exception.ResultRuntimeException;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.interceptor.WebMvcHandlerInterceptor;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.security.SecurityCredential;
import net.ideahut.springboot.security.WebMvcSecurity;
import net.ideahut.springboot.template.Application;
import net.ideahut.springboot.template.app.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;

@Component
@ComponentScan
class RequestHandlerInterceptor extends WebMvcHandlerInterceptor {
	
	private final AppProperties appProperties;
	private final AdminHandler adminHandler;
	private final WebMvcSecurity adminSecurity;
	private final SecurityCredential adminCredential;
	private final WebMvcApiService apiService;
	private final ApiAccess publicApiAccess;
	
	// set false, agar ApiService tidak melakukan pengecekan (development)
	// default ApiAccess Role = PUBLIC
	private static final boolean CHECK_BY_API_SERVICE = true;
	// set true, jika tidak ingin mengecek ApiRoleRequest / ApiPropvider request (development)
	private static final boolean ALLOW_ALL_REQUEST = false;
	
	@Autowired
	RequestHandlerInterceptor(
		AppProperties appProperties,
		AdminHandler adminHandler,
		@Qualifier(AppConstants.Bean.Security.ADMIN)
		WebMvcSecurity adminSecurity,
		@Qualifier(AppConstants.Bean.Credential.ADMIN)
		SecurityCredential adminCredential,
		WebMvcApiService apiService
	) {
		this.appProperties = appProperties;
		this.adminHandler = adminHandler;
		this.adminSecurity = adminSecurity;
		this.adminCredential = adminCredential;
		this.apiService = apiService;
		this.publicApiAccess = ApiAccess.unmodifiable(new ApiAccess().setApiRole(AppConstants.Default.API_ROLE));
	}

	@Override
	protected boolean isReady() {
		return Application.isReady();
	}
	
	@Override
	protected String publicBaseUrl() {
		return appProperties.getPublicBaseUrl();
	}

	@Override
	protected String publicAuditor() {
		return AppConstants.Default.AUDITOR;
	}
	
	@Override
	protected ApiAccess publicApiAccess() {
		return publicApiAccess;
	}
	
	@Override
	protected WebMvcApiService apiService() {
		return CHECK_BY_API_SERVICE ? apiService : null;
	}

	@Override
	protected AdminHandler adminHandler() {
		return adminHandler;
	}

	@Override
	protected WebMvcSecurity adminSecurity() {
		return adminSecurity;
	}

	@Override
	protected SecurityCredential adminCredential() {
		return adminCredential;
	}

	@Override
	protected boolean handleApi(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
		boolean isPublic = FrameworkHelper.isPublic(handlerMethod);
		ApiRequest apiRequest = apiService().getApiRequest(request, isPublic);
		ApiAccess apiAccess = apiService().getApiAccess(apiRequest);
		apiAccess = ObjectHelper.useOrDefault(apiAccess, this::publicApiAccess);
		if (!ALLOW_ALL_REQUEST && !isPublic) {
			boolean allowed = apiService().isApiAccessAllowed(apiAccess, handlerMethod);
			if (!allowed) {
				throw ResultRuntimeException.of(Result.error("REQ-00", "Request not allowed"));
			}
		}
		String auditor = apiAuditor(apiAccess);
		AuditInfo.fromContext().setAuditor(auditor);
		RequestContext.currentContext()
		.setAttribute(ApiRequest.class, apiRequest)
		.setAttribute(ApiAccess.class, apiAccess);
		return true;
	}	
	
}
