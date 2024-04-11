package net.ideahut.springboot.template.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.crud.CrudAction;
import net.ideahut.springboot.crud.CrudHandler;
import net.ideahut.springboot.crud.CrudPermission;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.util.RequestUtil;

@ComponentScan
@RestController
@RequestMapping("/crud")
class CrudController extends net.ideahut.springboot.crud.CrudController {
	
	@Autowired
	private CrudHandler handler;
	
	@Autowired
	private CrudPermission permission;
	
	
	@Override
	protected CrudHandler handler() {
		return handler;
	}
	
	
	
	/*
	 * INFO
	 * - Tipe Id: STANDARD, EMBEDDED, COMPOSITE
	 * - Logical: AND, OR
	 * - Condition: ANY_LIKE, LIKE, BETWEEN, dll
	 * - Match: EXACT (inner join), CONTAIN (left join)
	 * - CrudAction: SINGLE, PAGE, LIST, CREATE, UPDATE, DELETE, dll
	 */
	@Public
	@GetMapping(value = "/info/constant")
	protected Result infoConstant() {
		return super.constant();
	}	
	
	
	
	/*
	 * BODY ACTION
	 */
	@PostMapping(value = "/action/{action}")
	protected Result action(
		@PathVariable("action") String action,
		HttpServletRequest request
	) throws Exception {
		byte[] data = RequestUtil.getBodyAsBytes(request);
		return super.body(CrudAction.valueOf(action.toUpperCase()), data);
	}
	
	
	
	/*
	 * PARAMETER
	 */
	@RequestMapping(
		value = "/parameter/{action}", 
		method = { 
			RequestMethod.GET, 
			RequestMethod.POST, 
			RequestMethod.PUT, 
			RequestMethod.DELETE 
		}
	)
	protected Result parameter(
		@PathVariable("action") String action,
		HttpServletRequest request
	) throws Exception {
		return super.parameter(CrudAction.valueOf(action.toUpperCase()), request);		
	}
	
	
	
	/*
	 * OBJECT (CrudAction.SINGLE)
	 */
	@GetMapping(value = "/rest/{name}/{id}")
	protected Result object(
		@PathVariable("name") String name, 
		@PathVariable("id") String id,
		@RequestParam(value = "manager", required = false) String manager
	) {
		return super.object(manager, name, id);
	}
	
	
	
	/*
	 * COLLECTION (CrudAction.PAGE)
	 */
	@GetMapping(value = "/rest/{name}/{index}/{size}")
	protected Result collection(
		@PathVariable("name") String name, 
		@PathVariable("index") Integer index, 
		@PathVariable("size") Integer size,
		@RequestParam(value = "manager", required = false) String manager,
		@RequestParam(value = "count", required = false) String count,
		@RequestParam(value = "filters", required = false) String filters,
		@RequestParam(value = "orders", required = false) String orders,
		@RequestParam(value = "fields", required = false) String fields,
		@RequestParam(value = "loads", required = false) String loads		
	) {
		return super.collection(manager, name, index, size, count, filters, orders, fields, loads);
	}
	
	
	
	/*
	 * CREATE
	 */
	@PostMapping(value = "/rest/{name}")
	protected Result create(
		@PathVariable("name") String name,
		@RequestParam(value = "manager", required = false) String manager,
		@RequestParam(value = "value", required = false) String value,
		HttpServletRequest request
	) throws Exception {
		byte[] data = RequestUtil.getBodyAsBytes(request);
		return super.create(manager, name, value, data);
	}
	
	
	
	/*
	 * UPDATE
	 */
	@PutMapping(value = "/rest/{name}/{id}")
	protected Result update(
		@PathVariable("name") String name,
		@PathVariable("id") String id,
		@RequestParam(value = "manager", required = false) String manager,
		@RequestParam(value = "value", required = false) String value,
		HttpServletRequest request
	) throws Exception {
		byte[] data = RequestUtil.getBodyAsBytes(request);
		return super.update(manager, name, id, value, data);
	}
	
	
	
	
	/*
	 * DELETE 
	 */
	@DeleteMapping(value = "/rest/{name}/{id}")
	protected Result delete(
		@PathVariable("name") String name,
		@PathVariable("id") String id,
		@RequestParam(value = "manager", required = false) String manager
	) {
		return super.delete(manager, name, id);
	}

	
	
	/*
	 * Crud Permission bisa di level Handler ataupun di lever Controller.
	 * Untuk di level Handler akan berlaku disetiap penggunaan CrudHandler
	 * Untuk di level Controller hanya akan berlaku di setiap pemanggilan endpoint Crud
	 */
	@Override
	protected CrudPermission permission() {
		return permission;
	}
	
}
