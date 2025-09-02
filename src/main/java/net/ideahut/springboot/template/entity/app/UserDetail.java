package net.ideahut.springboot.template.entity.app;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAudit;

@Audit
@Entity
@Table(name = "user_detail")
@Setter
@Getter
@SuppressWarnings("serial")
public class UserDetail extends EntityAudit {

	@Id
	@Column(name = "user_id", unique = true, nullable = false, length = 64)
	private String userId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(
		name = "user_id",
		insertable = false, 
		updatable = false, 
		foreignKey = @ForeignKey(name = "fk_user_detail__user")
	)
	private User user;
	
	@Column(name = "fullname", nullable = false)
	private String fullname;
	
	@Column(name = "gender", length = 1)
	private Character gender; // Constants.Profile.Gender
	
	@Column(name = "description")
	private String description;
	
	public UserDetail() {}
	
	public UserDetail(String userId) {
		this.userId = userId;
	}

}
