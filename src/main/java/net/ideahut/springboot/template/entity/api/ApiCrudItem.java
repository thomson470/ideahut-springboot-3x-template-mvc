package net.ideahut.springboot.template.entity.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.ApiExclude;
import net.ideahut.springboot.api.entity.EntCrudItem;

@ApiExclude
@Entity
@Table(name = "api_crud_item")
@Setter
@Getter
@SuppressWarnings("serial")
public class ApiCrudItem extends EntCrudItem {

	public ApiCrudItem() {
		super();
	}

	public ApiCrudItem(String id) {
		super(id);
	}
	
}
