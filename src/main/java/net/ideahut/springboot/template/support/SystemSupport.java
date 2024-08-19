package net.ideahut.springboot.template.support;

import java.util.HashMap;
import java.util.Map;

import net.ideahut.springboot.entity.EntityPostListener;
import net.ideahut.springboot.sysparam.SysParamHandler;
import net.ideahut.springboot.sysparam.SysParamReloader;
import net.ideahut.springboot.sysparam.SysParamRemover;
import net.ideahut.springboot.sysparam.entity.SysParam;

public class SystemSupport {
	
	private SystemSupport() {}
	
	public static Map<Class<?>, EntityPostListener> getEntityPostListeners(
		SysParamHandler sysParamHandler
	) {
		Map<Class<?>, EntityPostListener> listeners = new HashMap<>();
		
		// SysParam
		listeners.put(SysParam.class, new EntityPostListener() {
			@Override
			public void onPostDelete(Object entity) {
				if (sysParamHandler instanceof SysParamRemover) {
					SysParam sysParam = (SysParam) entity;
					((SysParamRemover)sysParamHandler).removeSysParam(sysParam.getSysCode(), sysParam.getParamCode());
				}
			}
			@Override
			public void onPostInsert(Object entity) {
				onPostUpdate(entity);
			}
			@Override
			public void onPostUpdate(Object entity) {
				if (sysParamHandler instanceof SysParamReloader) {
					SysParam sysParam = (SysParam) entity;
					((SysParamReloader)sysParamHandler).reloadSysCodes(sysParam.getSysCode());
				}
			}
			
		});
		return listeners;
	}

}
