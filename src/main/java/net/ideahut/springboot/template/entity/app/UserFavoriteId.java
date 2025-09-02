package net.ideahut.springboot.template.entity.app;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter
@Getter
@SuppressWarnings("serial")
public class UserFavoriteId implements Serializable {

	@Column(name = "user_id", nullable = false, length = 64)
	private String userId;
	
	// other user
	@Column(name = "other_id", nullable = false, length = 64)
	private String otherId;
	
	public UserFavoriteId() {}
	
	public UserFavoriteId(String userId, String otherId) {
		this.userId = userId;
		this.otherId = otherId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(otherId, userId);
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
		UserFavoriteId other = (UserFavoriteId) obj;
		return Objects.equals(otherId, other.otherId) && Objects.equals(userId, other.userId);
	}
	
}
