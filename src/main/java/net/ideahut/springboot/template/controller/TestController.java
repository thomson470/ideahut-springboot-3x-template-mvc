package net.ideahut.springboot.template.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.audit.AuditHandler;
import net.ideahut.springboot.audit.AuditInfo;
import net.ideahut.springboot.crud.Condition;
import net.ideahut.springboot.crud.CrudBuilder;
import net.ideahut.springboot.crud.CrudHandler;
import net.ideahut.springboot.crud.CrudRequest;
import net.ideahut.springboot.crud.CrudRest;
import net.ideahut.springboot.crud.Filter;
import net.ideahut.springboot.crud.Join;
import net.ideahut.springboot.crud.Match;
import net.ideahut.springboot.crud.Relation;
import net.ideahut.springboot.crud.Stack;
import net.ideahut.springboot.entity.EntityInfo;
import net.ideahut.springboot.entity.EntityReplica;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.SessionCallable;
import net.ideahut.springboot.entity.TrxManagerInfo;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.object.MapStringObject;
import net.ideahut.springboot.object.Page;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.security.RedisMemoryCredential;
import net.ideahut.springboot.sysparam.SysParamDto;
import net.ideahut.springboot.sysparam.SysParamHandler;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.entity.AutoGenStrIdHardDel;
import net.ideahut.springboot.template.entity.CompositeHardDel;
import net.ideahut.springboot.template.entity.EmbeddedWithoutAttribute;
import net.ideahut.springboot.template.entity.EmbededId;
import net.ideahut.springboot.template.entity.LongIdJoinComposite;
import net.ideahut.springboot.template.entity.ManualGenStrIdSoftDel;
import net.ideahut.springboot.template.entity.User;
import net.ideahut.springboot.template.entity.UserDetail;
import net.ideahut.springboot.util.RequestUtil;
import net.ideahut.springboot.util.TimeUtil;

@ComponentScan
@RestController
@RequestMapping("/test")
class TestController {
	
	@Autowired
	private DataMapper dataMapper;
	@Autowired
	private EntityTrxManager entityTrxManager;
	@Autowired
	private CrudHandler crudHandler;
	@Autowired
	@Qualifier(AppConstants.Bean.Task.COMMON)
	private TaskHandler taskHandler;
	@Autowired
	private AuditHandler auditHandler;
	
	@Public
	@GetMapping(value = "/halo")
	protected String halo() throws Exception {
		throw new Exception("halooo");
	}
	
	@Public
	@GetMapping(value = "/audit/bytes")
	protected Result auditBytes() throws Exception {
		AuditInfo.context().setId("spring_sample");
		EmbededId id = new EmbededId();
		id.setCode("HALOOO");
		id.setType(999);
		byte[] data = dataMapper.writeAsBytes(id, DataMapper.JSON);
		auditHandler.save("TEST", data);
		return Result.success();
	}

	
	@Public
	@GetMapping(value = "/replica")
	protected Result replica() throws Exception {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		EntityInfo entityInfo = trxManagerInfo.getEntityInfo(UserDetail.class);
		EntityInfo refEntityInfo = trxManagerInfo.getEntityInfo(User.class);
		//List<String> sqls = EntityReplica.getSQL(entityInfo, 2, refEntityInfo);
		//return Result.success(sqls);
		List<EntityReplica.Creation> creations = EntityReplica.create(entityInfo, 2);
		return Result.success(creations);
	}
	
	@Public
	@GetMapping(value = "/crud")
	protected Result crud() throws Exception {
		/*
		byte[] data = IOUtils.toByteArray(RequestContext.currentContext().getRequest().getInputStream());
		CrudRequest crudRequest = crudHandler.getCrudRequest(data);
		CrudSelect.of(entityTrxManager, crudRequest);
		*/
		CrudBuilder builder = CrudBuilder.of(entityTrxManager.getDefaultTrxManagerInfo(), EmbeddedWithoutAttribute.class)
		.setId(new EmbededId());
		//.addJoin(Join.of(null, null).setReplica(null))
		
		return Result.success();
	}
	
	@Public
	@GetMapping(value = "/query")
	protected Result query() {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		CompositeHardDel o = trxManagerInfo.transaction(new SessionCallable<CompositeHardDel>() {
			@Override
			public CompositeHardDel call(Session session) throws Exception {
				// TODO Auto-generated method stub
				return session.get(CompositeHardDel.class, new CompositeHardDel(1, "A"));
			}
		});
		return Result.success(o);
		/*
		List<?> list = trxManagerInfo.transaction(new SessionCallable<List<?>>() {
			@Override
			public List<?> call(Session session) throws Exception {
				Query<?> query = session.createQuery("select a, b.status, b.username from UserDetail a inner join User b on b.userId=a.userId where b.username like ?1");
				query.setParameter(1, "%admin%");
				List<?> list = query.getResultList();
				trxManagerInfo.loadLazy(list, UserDetail.class);
				return list;
			}
		});
		return Result.success(list);
		*/
	}
	
	/*
	@Public
	@GetMapping(value = "/select")
	protected Result select() {
		CrudSelect<User> select = CrudSelect.of(entityTrxManager, new CrudRequest().setType(UserDetail.class));
		return Result.success();
	}
	*/
	
	@Public
	@GetMapping(value = "/crud/rest")
	protected Result crud_rest() throws Exception {
		LongIdJoinComposite value = new LongIdJoinComposite();
		value.setName("test");
		value.setDescription("description");
		
		MapStringObject mso = new MapStringObject();
		mso.put("name", "test");
		mso.put("description", null);
		
		CrudRest rest = CrudRest.of("LongIdJoinComposite")
		.setPrettyPrint(true)
		.setPage(Page.of(1, 100))
		.addJoin(
			Join.of("CompositeHardDel")
			.setStore("composite")
			.setMatch(Match.CONTAIN)
			.addRelation(new Relation().setSource("composite.type").setTarget("type"))
			.addRelation(new Relation().setSource("composite.code").setTarget("code"))
		)
		.addStack(
			Stack.of("XXXX")
			.addRelation(new Relation().setSource("xxx").setTarget("yyy"))
			.addValue(mso)
			.addValue(value)
		)
		.addValue(value)
		.addValue(mso)
		.addFilter(Filter.and("createdOn", Condition.NOT_NULL))
		.addOrder("-createdOn");
		//CrudRequest request = rest.getRequest();
		System.out.println(rest.toJsonString());
		return Result.success();
	}
	
	@Public
	@GetMapping(value = "/insert")
	protected Result insert() throws Exception {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		AutoGenStrIdHardDel value = new AutoGenStrIdHardDel();
		value.setCreatedOn(TimeUtil.currentEpochMillis());
		value.setUpdatedOn(value.getCreatedOn());
		value.setName("Tes Via Controller - " + System.nanoTime());
		value.setIsActive('Y');
		//CrudRequest request = CrudBuilder.of(trxManagerInfo, AutoGenStrIdHardDel.class)
		//.addValue(value).setUseNative(true).build();
		//CrudResult cres = crudHandler.execute(CrudAction.CREATE, request);
		/*
		for (int i = 0; i < 500_000; i++) {
			Integer j = i;
			taskHandler.execute(new Runnable() {
				@Override
				public void run() {
					System.out.println(j);
					AutoGenStrIdHardDel value = new AutoGenStrIdHardDel();
					value.setCreatedOn(TimeUtil.currentEpochMillis());
					value.setUpdatedOn(value.getCreatedOn());
					value.setName("Tes Via Controller - " + System.nanoTime());
					value.setIsActive('Y');
					CrudRequest request = CrudBuilder.of(trxManagerInfo, AutoGenStrIdHardDel.class)
					.addValue(value).setUseNative(true).build();
					crudHandler.execute(CrudAction.CREATE, request);
				}
			});
		}
		*/
		return Result.success();
		
		/*
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		EntityInfo entityInfo = trxManagerInfo.getEntityInfo(AutoGenStrIdHardDel.class);
		for (int i = 0; i < 500_000; i++) {
			taskHandler.execute(new Runnable() {
				@Override
				public void run() {
					AutoGenStrIdHardDel o = new AutoGenStrIdHardDel();
					o.setCreatedOn(TimeUtil.currentEpochMillis());
					o.setUpdatedOn(o.getCreatedOn());
					o.setName("Tes Via Controller");
					o.setIsActive('Y');
					EntityNative.save(entityInfo, null, o);
				}
			});
		}
		return Result.success();
		*/
		/*
		AutoGenStrIdHardDel o = new AutoGenStrIdHardDel();
		o.setCreatedOn(TimeUtil.currentEpochMillis());
		o.setUpdatedOn(o.getCreatedOn());
		o.setName("Tes Via Controller");
		o.setIsActive('Y');
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		AutoGenStrIdHardDel n = trxManagerInfo.transaction(true, new SessionCallable<AutoGenStrIdHardDel>() {
			@Override
			public AutoGenStrIdHardDel call(Session session) throws Exception {
				session.save(o);
				return o;
			}
		});
		return Result.success(n);
		*/
	}
	
	@Public
	@PostMapping("/crud/request")
	protected Result crudRequest() throws Exception {
		byte[] data = IOUtils.toByteArray(RequestUtil.getRequest().getInputStream());		
		CrudRequest crudRequest = crudHandler.getRequest(data);
		return Result.success(crudRequest);
	}
	
	
	@Autowired
	private RedisMemoryCredential adminCredential;
	@Public
	@GetMapping("/admin/reload")
	protected void adminReload() throws Exception {
		adminCredential.reloadBean();
	}
	
	@Autowired
	private SysParamHandler sysParamHandler;
	@Public
	@GetMapping("/sysparam/maps")
	protected Map<String, Map<String, SysParamDto>> sysParamMaps() throws Exception {
		return sysParamHandler.getSysParamMaps(Arrays.asList("ARTICLE", "MULTIMEDIA"));
	}
	@Public
	@GetMapping("/sysparam/value")
	protected SysParamDto sysParamValue() {
		return sysParamHandler.getSysParam("SENTIMENT", "DEFAULT_ANALYZER_ID");
	}
	
	
	
	
	@GetMapping("/async/{id}")
    @Async
    protected CompletableFuture<Result> getResultAsync(
    	@PathVariable("id") String id
    ) {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		ManualGenStrIdSoftDel data = trxManagerInfo.transaction(new SessionCallable<ManualGenStrIdSoftDel>() {
			@Override
			public ManualGenStrIdSoftDel call(Session session) throws Exception {
				return session.get(ManualGenStrIdSoftDel.class, id);
			}
		});
		return CompletableFuture.completedFuture(Result.success(data).setInfo("thread", Thread.currentThread().getId()));
    }
	
	@GetMapping("/sync/{id}")
    @Async
    protected Result getResultSync(
    	@PathVariable("id") String id
    ) {
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		ManualGenStrIdSoftDel data = trxManagerInfo.transaction(new SessionCallable<ManualGenStrIdSoftDel>() {
			@Override
			public ManualGenStrIdSoftDel call(Session session) throws Exception {
				return session.get(ManualGenStrIdSoftDel.class, id);
			}
		});
		return Result.success(data).setInfo("thread", Thread.currentThread().getId());
    }
	
	@GetMapping("/emitter")
	protected ResponseBodyEmitter emitter() {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		ResponseBodyEmitter emitter = new ResponseBodyEmitter();
		// Save the emitter somewhere..
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					emitter.send("hallo", MediaType.TEXT_PLAIN);
					emitter.send("Bro", MediaType.TEXT_PLAIN);
					Thread.sleep(2000);
					emitter.send(Result.success("OK"), MediaType.APPLICATION_JSON);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					emitter.complete();
					executor.shutdownNow();
				}
				
			}
		});
		return emitter;
	}
	
	// https://www.baeldung.com/spring-mvc-sse-streams
	// https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-async.html
	// https://medium.com/@david.richards.tech/sse-server-sent-events-using-a-post-request-without-eventsource-1c0bd6f14425
	@GetMapping("/sseemitter")
	protected ResponseEntity<SseEmitter> sseemitter() {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		SseEmitter emitter = new SseEmitter(500L);
		// Save the emitter somewhere..
		executor.execute(new Runnable() {
			@Override
			public void run() {
				/*
				try {
					emitter.send("hallo", MediaType.TEXT_PLAIN);
					emitter.send("Bro", MediaType.TEXT_PLAIN);
					Thread.sleep(2000);
					emitter.send(Result.success("OK"), MediaType.APPLICATION_JSON);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					emitter.complete();
					executor.shutdownNow();
				}
				*/
				try {
					TimeUnit.SECONDS.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				emitter.completeWithError(new Exception("Error Bro"));
				
			}
		});
		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(emitter);
	}
	
	@GetMapping("/streaming")
	@Async
	protected StreamingResponseBody streaming() {
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream outputStream) throws IOException {
				outputStream.write("hallo\n".getBytes());
				outputStream.write("bro\n".getBytes());
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outputStream.write("Finish".getBytes());
			}
		};
	}
	
	@GetMapping(value = "/stream/data")
	protected ResponseEntity<StreamingResponseBody> streamData() {
		StreamingResponseBody responseBody = response -> {
			for (int i = 1; i <= 1000; i++) {
				try {
					Thread.sleep(10);
					response.write(("Data stream line - " + i + "\n").getBytes());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(responseBody);
	}
	
}
