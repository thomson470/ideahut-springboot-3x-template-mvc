package net.ideahut.springboot.template.entity.api;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.ApiExclude;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.api.entity.EntCrudField;
import net.ideahut.springboot.api.entity.EntCrudFieldId;

@ApiExclude
@Audit
@Entity
@Table(name = "api_crud_field")
@Setter
@Getter
@SuppressWarnings("serial")
public class ApiCrudField extends EntCrudField {
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "crud_code", 
		nullable = false, 
		insertable = false, 
		updatable = false, 
		foreignKey = @ForeignKey(name = "fk_api_crud_field__crud")
	)
	private ApiCrud crud;
	
	public ApiCrudField() {}
	
	public ApiCrudField(EntCrudFieldId id) {
		super(id);
	}
	
}
