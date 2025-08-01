package net.ideahut.springboot.template.entity.app;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAudit;

@Audit
@Entity
@Table(name = "menu_role")
@Setter
@Getter
@SuppressWarnings("serial")
public class MenuRole extends EntityAudit {

	@EmbeddedId
	@AttributeOverride(name = "menuCode", column = @Column(name = "menu_code", nullable = false, length = 64))
	@AttributeOverride(name = "menuType", column = @Column(name = "menu_type", nullable = false, length = 16))
	@AttributeOverride(name = "roleCode", column = @Column(name = "role_code", nullable = false, length = 64))
	private MenuRoleId id;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns(
		value = {
			@JoinColumn(name = "menu_code", referencedColumnName = "menu_code", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "menu_type", referencedColumnName = "menu_type", nullable = false, insertable = false, updatable = false)
		},
		foreignKey = @ForeignKey(name = "fk_menu_role__menu")
	)
	private Menu menu;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "role_code", 
		nullable = false, 
		insertable = false, 
		updatable = false, 
		foreignKey = @ForeignKey(name = "fk_menu_role__role")
	)
	private Role role;
	
	public MenuRole() {}
	
	public MenuRole(MenuRoleId id) {
		this.id = id;
	}
	
}
