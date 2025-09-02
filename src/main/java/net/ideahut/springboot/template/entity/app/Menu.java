package net.ideahut.springboot.template.entity.app;

import java.util.List;

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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAuditSoftDelete;

@Audit
@Entity
@Table(name = "menu")
@Setter
@Getter
@SuppressWarnings("serial")
public class Menu extends EntityAuditSoftDelete {

	@EmbeddedId
	@AttributeOverride(name = "menuCode", column = @Column(name = "menu_code", nullable = false, length = 64))
	@AttributeOverride(name = "menuType", column = @Column(name = "menu_type", nullable = false, length = 16)) 
	private MenuId id;
	
	@Column(name = "title", nullable = false, length = 100)
	private String title;
	
	@Column(name = "link", length = 100)
	private String link;
	
	@Column(name = "icon", length = 512)
	private String icon;
	
	@Lob
	//@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "description")
	private String description;
	
	@Column(name = "is_active", nullable = false, length = 1)
	private Character isActive;
	
	@Column(name = "seqno", nullable = false)
	private Long seqno;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JoinColumns(
		value = { 
			@JoinColumn(name = "parent_code", referencedColumnName = "menu_code", nullable = true, insertable = true, updatable = true),
			@JoinColumn(name = "parent_type", referencedColumnName = "menu_type", nullable = true, insertable = true, updatable = true) 
		},
		foreignKey = @ForeignKey(name = "fk_menu__parent")
	)
	private Menu parent;
	
	@Transient
	private List<Menu> children;
	
	public Menu() {}
	
	public Menu(MenuId id) {
		this.id = id;
	}
	
}
