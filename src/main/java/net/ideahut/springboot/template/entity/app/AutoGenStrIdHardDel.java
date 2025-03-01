package net.ideahut.springboot.template.entity.app;

import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.crud.Condition;
import net.ideahut.springboot.entity.EntityAudit;
import net.ideahut.springboot.generator.OdtIdGenerator;

@Audit
@Entity
@Table(name = "auto_gen_str_id_hard_del")
@Setter
@Getter
@SuppressWarnings("serial")
public class AutoGenStrIdHardDel extends EntityAudit {
	
	@Id
	@GeneratedValue(generator = OdtIdGenerator.NAME)
	@GenericGenerator(name = OdtIdGenerator.NAME, type = OdtIdGenerator.class)
	@Column(name = "id", unique = true, nullable = false, length = 64)
	private String id;
	
	@Column(name = "name", nullable = false, length = 128)
	private String name;
	
	@Column(name = "description")
	private String description;

	@Column(name = "is_active", nullable = false, length = 1)
	private Character isActive;
	
	//@Temporal(TemporalType.TIMESTAMP)
	//@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = AppConstants.Default.TIMEZONE)
	//@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //@Column(name = "date_", nullable = false)
    //private Date date;//-
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_", nullable = false)
    private LocalDateTime date;
	
	//@JsonFormat(pattern = "yyyy-MM-dd", timezone = AppConstants.Default.TIMEZONE)
	//@Column(name = "date_", nullable = false)
    //private LocalDate date;//-
	
	//@JsonFormat(pattern = "HH:mm:ss", timezone = AppConstants.Default.TIMEZONE)
	//@Column(name = "time_", nullable = false)
    //private LocalTime date;//-
	
	@Enumerated(EnumType.STRING)
	@Column(name = "condition_", nullable = false)
	private Condition condition;
	
	public AutoGenStrIdHardDel() {}
	
	public AutoGenStrIdHardDel(String id) {
		this.id = id;
	}
	
}
