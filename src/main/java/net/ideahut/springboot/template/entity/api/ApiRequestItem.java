package net.ideahut.springboot.template.entity.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.ApiExclude;
import net.ideahut.springboot.api.entity.EntRequestItem;

@ApiExclude
@Entity
@Table(name = "api_request_item")
@Setter
@Getter
@SuppressWarnings("serial")
public class ApiRequestItem extends EntRequestItem {

	public ApiRequestItem() {
		super();
	}

	public ApiRequestItem(String id) {
		super(id);
	}
	
}
