package net.ideahut.springboot.template.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import net.ideahut.springboot.crud.CrudAction;
import net.ideahut.springboot.crud.CrudBuilder;
import net.ideahut.springboot.crud.CrudHandler;
import net.ideahut.springboot.crud.CrudResult;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.SessionCallable;
import net.ideahut.springboot.entity.TrxManagerInfo;
import net.ideahut.springboot.job.JobGroupDto;
import net.ideahut.springboot.job.JobInstanceDto;
import net.ideahut.springboot.job.JobService;
import net.ideahut.springboot.job.JobTriggerDto;
import net.ideahut.springboot.job.JobTypeDto;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.entity.JobGroup;
import net.ideahut.springboot.template.entity.JobInstance;
import net.ideahut.springboot.template.entity.JobTrigger;
import net.ideahut.springboot.template.entity.JobTriggerConfiguration;
import net.ideahut.springboot.template.entity.JobType;
import net.ideahut.springboot.util.FrameworkUtil;

@Service
public class JobServiceImpl implements JobService {
	
	@Autowired
	private EntityTrxManager entityTrxManager;
	@Qualifier(AppConstants.Bean.Task.COMMON)
	@Autowired
	private TaskHandler taskHandler;
	@Autowired
	private CrudHandler crudHandler;

	@Override
	public String getTraceKey() {
		return "traceId";
	}

	@Override
	public List<JobInstanceDto> getInstances() {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		CrudResult crudResult = CrudBuilder
		.of(trxManagerInfo, JobInstance.class)
		.addOrder("instanceId")
		.execute(crudHandler, CrudAction.LIST);
		List<JobInstance> list = crudResult.getValue();
		List<JobInstanceDto> dtos = new ArrayList<>();
		if (list != null) {
			while(!list.isEmpty()) {
				JobInstance entity = list.remove(0);
				JobInstanceDto dto = new JobInstanceDto();
				BeanUtils.copyProperties(entity, dto);
				dtos.add(dto);
			}
		}
		return dtos;
	}
	
	@Override
	public List<JobGroupDto> getGroups(String instanceId, Boolean isActive) {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		List<JobGroup> list = trxManagerInfo.transaction(new SessionCallable<List<JobGroup>>() {
			@Override
			public List<JobGroup> call(Session session) throws Exception {
				List<Object> parameters = new ArrayList<>();
				String hql = 
				"select b " + 
				"from JobTrigger a " + 
				"join a.group b " + 
				"where 1=1 ";
				if (isActive != null) {
					Character active = Boolean.TRUE.equals(isActive) ? AppConstants.Boolean.YES : AppConstants.Boolean.NO;
					parameters.add(active);
					hql += "and a.isActive=?" + parameters.size() + " and b.isActive=?" + parameters.size() + " ";							
				}
				hql += "and (a.instance.instanceId is null ";
				String iid = instanceId != null ? instanceId.trim() : "";
				if (!iid.isEmpty()) {
					parameters.add(iid);
					hql += "or a.instance.instanceId=?" + parameters.size();
					
				}
				hql += ") ";
				Query<JobGroup> query = session.createQuery(hql, JobGroup.class);
				for (int i = 0; i < parameters.size(); i++) {
					query.setParameter(i + 1, parameters.get(i));
				}
				List<JobGroup> list = query.getResultList();
				trxManagerInfo.loadLazy(list, JobGroup.class);
				return list;
			}
		});
		List<JobGroupDto> groups = new ArrayList<>();
		while(!list.isEmpty()) {
			JobGroup eGroup = list.remove(0);
			JobGroupDto group = new JobGroupDto();
			BeanUtils.copyProperties(eGroup, group);
			groups.add(group);
		}
		return groups;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<JobGroupDto> getGroupsAndTriggers(String instanceId, Boolean isActive, Collection<String> groupIds, Boolean includeConfigs) {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		Future<Object[]> futureTrigger = taskHandler.submit(new Callable<Object[]>() {
			@Override
			public Object[] call() throws Exception {
				return trxManagerInfo.transaction(new SessionCallable<Object[]>() {
					@Override
					public Object[] call(Session session) throws Exception {
						List<Object> parameters = new ArrayList<>();
						String hql = 
						"select a, b, c " + 
						"from JobTrigger a " + 
						"join a.type b " +
						"join a.group c " + 
						"where 1=1 ";
						if (isActive != null) {
							Character active = Boolean.TRUE.equals(isActive) ? AppConstants.Boolean.YES : AppConstants.Boolean.NO;
							parameters.add(active);
							hql += "and a.isActive=?" + parameters.size() + " and c.isActive=?" + parameters.size() + " ";							
						}
						hql += "and (a.instance.instanceId is null ";
						String iid = instanceId != null ? instanceId.trim() : "";
						if (!iid.isEmpty()) {
							parameters.add(iid);
							hql += "or a.instance.instanceId=?" + parameters.size();
							
						}
						hql += ") ";
						if (groupIds != null && !groupIds.isEmpty()) {
							parameters.add(groupIds);
							hql += "and c.groupId in (?" + parameters.size() + ") ";
							
						}
						Query<Object[]> query = session.createQuery(hql, Object[].class);
						for (int i = 0; i < parameters.size(); i++) {
							query.setParameter(i + 1, parameters.get(i));
						}
						Map<String, Integer> groupIndexes = new HashMap<>();
						Map<String, Integer[]> triggerIndexes = new HashMap<>();						
						List<JobGroupDto> groups = new ArrayList<>();
						
						List<Object[]> items = query.getResultList();
						while (!items.isEmpty()) {
							Object[] item = items.remove(0);
							
							// trigger
							JobTrigger eTrigger = (JobTrigger) item[0];
							trxManagerInfo.loadLazy(eTrigger, JobTrigger.class);
							JobTriggerDto trigger = new JobTriggerDto();
							BeanUtils.copyProperties(eTrigger, trigger, "configurations");
							trigger.setConfigurations(new HashMap<>());
							
							// type
							JobType eType = (JobType) item[1];
							trxManagerInfo.loadLazy(eType, JobType.class);
							JobTypeDto type = new JobTypeDto();
							BeanUtils.copyProperties(eType, type, "parameters", "triggers");
							trigger.setType(type);
							
							// group
							JobGroup eGroup = (JobGroup) item[2];
							trxManagerInfo.loadLazy(eGroup, JobGroup.class);
							JobGroupDto group = new JobGroupDto();
							BeanUtils.copyProperties(eGroup, group, "triggers");
							trigger.setGroup(new JobGroupDto(group.getGroupId()));
							
							Integer groupIndex = groupIndexes.getOrDefault(group.getGroupId(), null);
							if (groupIndex == null) {
								groupIndex = groups.size();
								groups.add(group);
								groupIndexes.put(group.getGroupId(), groupIndex);
								group.setTriggers(new ArrayList<>());
							}
							group = groups.get(groupIndex);
							Integer[] triggerIndex = triggerIndexes.getOrDefault(trigger.getTriggerId(), null);
							if (triggerIndex == null) {
								triggerIndex = new Integer[] {groupIndex, group.getTriggers().size()};
								triggerIndexes.put(trigger.getTriggerId(), triggerIndex);
								group.getTriggers().add(trigger);
							}
						}
						groupIndexes.clear();
						return new Object[] {groups, triggerIndexes};
					}
				});
			}
		});
		Future<List<JobTriggerConfiguration>> futureConfig = null;
		if (Boolean.TRUE.equals(includeConfigs)) {
			futureConfig = taskHandler.submit(new Callable<List<JobTriggerConfiguration>>() {
				@Override
				public List<JobTriggerConfiguration> call() throws Exception {
					return trxManagerInfo.transaction(new SessionCallable<List<JobTriggerConfiguration>>() {
						@Override
						public List<JobTriggerConfiguration> call(Session session) throws Exception {
							List<Object> parameters = new ArrayList<>();
							String hql = 
							"from JobTriggerConfiguration " + 
						    "where id.triggerId IN (" +
								"select a.triggerId " +
								"from JobTrigger a " + 
								"join a.type b " +
								"join a.group c " + 
								"where 1=1 ";
							if (isActive != null) {
								Character active = Boolean.TRUE.equals(isActive) ? AppConstants.Boolean.YES : AppConstants.Boolean.NO;
								parameters.add(active);
								hql += "and a.isActive=?" + parameters.size() + " and c.isActive=?" + parameters.size() + " ";							
							}
							hql += "and (a.instance.instanceId is null ";
							String iid = instanceId != null ? instanceId.trim() : "";
							if (!iid.isEmpty()) {
								parameters.add(iid);
								hql += "or a.instance.instanceId=?" + parameters.size();
								
							}
							hql += ") ";
							if (groupIds != null && !groupIds.isEmpty()) {
								parameters.add(groupIds);
								hql += "and c.groupId in (?" + parameters.size() + ") ";
								
							}					
							hql += ")";
							Query<JobTriggerConfiguration> query = session.createQuery(hql, JobTriggerConfiguration.class);
							for (int i = 0; i < parameters.size(); i++) {
								query.setParameter(i + 1, parameters.get(i));
							}
							List<JobTriggerConfiguration> configurations = query.getResultList();
							trxManagerInfo.loadLazy(configurations, JobTriggerConfiguration.class);
							return configurations;
						}
					});
				}
			});
		}
		try {
			Object[] objects = (Object[]) futureTrigger.get();
			List<JobGroupDto> groups = (List<JobGroupDto>) objects[0];
			if (groups != null && !groups.isEmpty() && futureConfig != null) {
				Map<String, Integer[]> indexes = (Map<String, Integer[]>) objects[1];
				List<JobTriggerConfiguration> configs = (List<JobTriggerConfiguration>) futureConfig.get();
				while (!configs.isEmpty()) {
					JobTriggerConfiguration config = configs.remove(0);
					Integer[] index = indexes.getOrDefault(config.getId().getTriggerId(), null);
					if (index != null) {
						groups.get(index[0]).getTriggers().get(index[1]).getConfigurations()
						.put(config.getId().getParameterName(), new JobTriggerDto.Configuration(config.getValue(), config.getBytes()));
					}
				}
				indexes.clear();
			}
			return groups;
		} catch (Exception e) {
			throw FrameworkUtil.exception(e);
		}
	}
	
	@Override
	public JobTriggerDto getTrigger(String triggerId, Boolean includeConfigs) {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		Future<JobTriggerDto> futureTrigger = taskHandler.submit(new Callable<JobTriggerDto>() {
			@Override
			public JobTriggerDto call() throws Exception {
				return trxManagerInfo.transaction(new SessionCallable<JobTriggerDto>() {
					@Override
					public JobTriggerDto call(Session session) throws Exception {
						String hql = 
						"select a, b, c " + 
						"from JobTrigger a " + 
						"join a.type b " +
						"join a.group c " + 
						"where a.triggerId=?1 ";
						
						Query<Object[]> query = session.createQuery(hql, Object[].class);
						query.setParameter(1, triggerId);
						Object[] item = (Object[]) query.uniqueResult();
						if (item == null) {
							return null;
						}
						
						// trigger
						JobTrigger eTrigger = (JobTrigger) item[0];
						trxManagerInfo.loadLazy(eTrigger, JobTrigger.class);
						JobTriggerDto trigger = new JobTriggerDto();
						BeanUtils.copyProperties(eTrigger, trigger, "configurations");
						trigger.setConfigurations(new HashMap<>());
						
						// type
						JobType eType = (JobType) item[1];
						trxManagerInfo.loadLazy(eType, JobType.class);
						JobTypeDto type = new JobTypeDto();
						BeanUtils.copyProperties(eType, type, "parameters", "triggers");
						trigger.setType(type);
						
						// group
						JobGroup eGroup = (JobGroup) item[2];
						trxManagerInfo.loadLazy(eGroup, JobGroup.class);
						JobGroupDto group = new JobGroupDto();
						BeanUtils.copyProperties(eGroup, group, "triggers");
						trigger.setGroup(group);
						
						return trigger;
					}			
				});
			}			
		});
		Future<List<JobTriggerConfiguration>> futureConfig = null;
		if (Boolean.TRUE.equals(includeConfigs)) {
			futureConfig = taskHandler.submit(new Callable<List<JobTriggerConfiguration>>() {
				@Override
				public List<JobTriggerConfiguration> call() throws Exception {
					return trxManagerInfo.transaction(new SessionCallable<List<JobTriggerConfiguration>>() {
						@Override
						public List<JobTriggerConfiguration> call(Session session) throws Exception {
							String hql = 
							"from JobTriggerConfiguration " + 
							"where id.triggerId=?1 ";
							Query<JobTriggerConfiguration> query = session.createQuery(hql, JobTriggerConfiguration.class);
							query.setParameter(1, triggerId);						
							List<JobTriggerConfiguration> configurations = query.getResultList();
							trxManagerInfo.loadLazy(configurations, JobTriggerConfiguration.class);
							return configurations;
						}
					});
				}			
			});
		}
		try {
			JobTriggerDto trigger = futureTrigger.get();
			if (trigger != null && futureConfig != null) {
				List<JobTriggerConfiguration> configs = futureConfig.get();
				while (!configs.isEmpty()) {
					JobTriggerConfiguration config = configs.remove(0);
					trigger.getConfigurations().put(config.getId().getParameterName(), new JobTriggerDto.Configuration(config.getValue(), config.getBytes()));
				}
			}
			return trigger;
		} catch (Exception e) {
			throw FrameworkUtil.exception(e);
		}
	}

	@Override
	public Map<String, JobTriggerDto> getTriggerMap(Collection<String> triggerIds) {
		if (triggerIds != null && !triggerIds.isEmpty()) {
			Set<String> ids = new LinkedHashSet<>(triggerIds);
			TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
			return trxManagerInfo.transaction(new SessionCallable<Map<String, JobTriggerDto>>() {
				@Override
				public Map<String, JobTriggerDto> call(Session session) throws Exception {
					Query<Object[]> query = session
					.createQuery(
						"select a, b " +
						"from JobTrigger a " +
						"join a.group b " +
						"where a.triggerId in (?1)", 
					Object[].class);
					List<Object[]> items = query.setParameter(1, ids).getResultList();
					Map<String, JobTriggerDto> triggers = new LinkedHashMap<>();
					while (!items.isEmpty()) {
						Object[] item = items.remove(0);
						JobTrigger eTrigger = (JobTrigger) item[0];
						trxManagerInfo.loadLazy(eTrigger);
						JobTriggerDto trigger = new JobTriggerDto();
						BeanUtils.copyProperties(eTrigger, trigger, "configurations");
						JobGroup eGroup = (JobGroup) item[1];
						trxManagerInfo.loadLazy(eGroup);
						JobGroupDto group = new JobGroupDto();
						BeanUtils.copyProperties(eGroup, group, "triggers");
						trigger.setGroup(group);
						triggers.put(trigger.getTriggerId(), trigger);
					}
					return triggers;
				}
			});
		}
		return new LinkedHashMap<>();
	}
	

	@Override
	public void setTypeIsRunning(String typeId, Boolean isRunning) {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		trxManagerInfo.transaction(true, new SessionCallable<Void>() {
			@Override
			public Void call(Session session) throws Exception {
				session.createMutationQuery("update JobType set isRunning = ?1 WHERE typeId = ?2")
				.setParameter(1, Boolean.TRUE.equals(isRunning) ? AppConstants.Boolean.YES : AppConstants.Boolean.NO)
				.setParameter(2, typeId)
				.executeUpdate();
				return null;
			}
		});
	}

	@Override
	public void resetAllTypeRunning() {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		trxManagerInfo.transaction(true, new SessionCallable<Void>() {
			@Override
			public Void call(Session session) throws Exception {
				session.createMutationQuery("update JobType set isRunning = ?1")
				.setParameter(1, AppConstants.Boolean.NO)
				.executeUpdate();
				return null;
			}
		});
	}

	@Override
	public void saveTypeResult(String typeId, Boolean isRunning, Long lastRunTime, String lastRunTriggerId, String lastRunData) {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		trxManagerInfo.transaction(true, new SessionCallable<Void>() {
			@Override
			public Void call(Session session) throws Exception {
				MutationQuery query = session.createMutationQuery(
					"update JobType set " + 
					"isRunning = ?1," + 
					"lastRunTime = ?2," + 
					"lastRunTriggerId = ?3 " +
					(lastRunData != null ? ", lastRunData = ?4 " : "") +
					"where typeId = " + (lastRunData != null ? "?5" : "?4")
				)
				.setParameter(1, Boolean.TRUE.equals(isRunning) ? AppConstants.Boolean.YES : AppConstants.Boolean.NO)
				.setParameter(2, lastRunTime)
				.setParameter(3, lastRunTriggerId);
				if (lastRunData != null) {				
					query.setParameter(4, lastRunData);
					query.setParameter(5, typeId);
				} else {
					query.setParameter(4, typeId);
				}
				query.executeUpdate();
				return null;
			}
		});
	}

	@Override
	public void saveTriggerResult(String triggerId, Long lastRunTime, String lastRunData) {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		trxManagerInfo.transaction(true, new SessionCallable<Void>() {
			@Override
			public Void call(Session session) throws Exception {
				MutationQuery query = session.createMutationQuery(
					"update JobTrigger set " + 
					"lastRunTime = ?1 " +
					(lastRunData != null ? ", lastRunData = ?2 " : "") +
					"where triggerId = "  + (lastRunData != null ? "?3" : "?2")
				)
				.setParameter(1, lastRunTime);
				if (lastRunData != null) {				
					query.setParameter(2, lastRunData);
					query.setParameter(3, triggerId);
				} else {
					query.setParameter(2, triggerId);
				}
				query.executeUpdate();
				return null;
			}
		});
	}		

}
