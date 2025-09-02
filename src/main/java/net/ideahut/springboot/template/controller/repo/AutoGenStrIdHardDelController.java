package net.ideahut.springboot.template.controller.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.entity.EntityInfo;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.TrxManagerInfo;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.entity.app.AutoGenStrIdHardDel;
import net.ideahut.springboot.template.repo.AutoGenStrIdHardDelRepo;

@Public
@ComponentScan
@RestController
@RequestMapping("/repo/AutoGenStrIdHardDel")
class AutoGenStrIdHardDelController {
	
	private final EntityTrxManager entityTrxManager;
	private final AutoGenStrIdHardDelRepo repo;
	
	@Autowired
	AutoGenStrIdHardDelController(
		EntityTrxManager entityTrxManager,
		AutoGenStrIdHardDelRepo repo
	) {
		this.entityTrxManager = entityTrxManager;
		this.repo = repo;
	}
	
	@GetMapping(value = "/{index}/{size}")
	Result page(
		@PathVariable("index") Integer index, 
		@PathVariable("size") Integer size,
		@RequestParam(value = "orders", required = false) String orders 
	) {
		Sort sort = Helper.getSort(orders);
		Pageable pageable = PageRequest.of(index - 1, size, sort);
		Page<AutoGenStrIdHardDel> page = repo.findAll(pageable);
		return Result.success(page);
	}
	
	@GetMapping(value = "/{id}")
	Result byId(
		@PathVariable("id") String id
	) {
		AutoGenStrIdHardDel entity = repo.findById(id).orElse(null);
		return Result.success(entity);
	}
	
	@PostMapping
	Result create(
		@RequestBody AutoGenStrIdHardDel data
	) {
		AutoGenStrIdHardDel entity = repo.save(data);
		return Result.success(entity);
	}
	
	@PutMapping(value = "/{id}")
	Result update(
		@PathVariable("id") String id,
		@RequestBody AutoGenStrIdHardDel data
	) {
		AutoGenStrIdHardDel entity = repo.findById(id).orElse(null);
		Assert.notNull(entity, "Entity is not found");
		TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
		EntityInfo entityInfo = trxManagerInfo.getEntityInfo(AutoGenStrIdHardDel.class);
		if (entityInfo.merge(data, entity, true, null)) {
			entity = repo.save(entity);
		}
		return Result.success(entity);
	}
	
	@DeleteMapping(value = "/{id}")
	Result delete(
		@PathVariable("id") String id
	) {
		repo.deleteById(id);
		return Result.success(id);
	}
	
}
