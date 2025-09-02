package net.ideahut.springboot.template.entity.app;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter 
@Getter
@SuppressWarnings("serial")
public class MenuRoleId implements java.io.Serializable {

	@Column(name = "menu_code", nullable = false, length = 64)
	private String menuCode;
	
	// mobile, portal, desktop
	@Column(name = "menu_type", nullable = false, length = 16)
	private String menuType; 
	
	@Column(name = "role_code", nullable = false, length = 64)
	private String roleCode;	

	public MenuRoleId() {}

	public MenuRoleId(String menuCode, String menuType, String roleCode) {
		this.menuCode = menuCode;
		this.menuType = menuType;
		this.roleCode = roleCode;
	}

	@Override
	public int hashCode() {
		return Objects.hash(menuCode, menuType, roleCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MenuRoleId other = (MenuRoleId) obj;
		return Objects.equals(menuCode, other.menuCode) && Objects.equals(menuType, other.menuType)
				&& Objects.equals(roleCode, other.roleCode);
	}	

}
