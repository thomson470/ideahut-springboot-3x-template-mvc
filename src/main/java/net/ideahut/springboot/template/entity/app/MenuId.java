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
public class MenuId implements java.io.Serializable {

	@Column(name = "menu_code", nullable = false, length = 64)
	private String menuCode;
	
	// mobile, portal, desktop
	@Column(name = "menu_type", nullable = false, length = 16)
	private String menuType;

	public MenuId() {}

	public MenuId(String menuCode, String menuType) {
		this.menuCode = menuCode;
		this.menuType = menuType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(menuCode, menuType);
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
		MenuId other = (MenuId) obj;
		return Objects.equals(menuCode, other.menuCode) && Objects.equals(menuType, other.menuType);
	}	

}
