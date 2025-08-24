package net.ideahut.springboot.template.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import net.ideahut.springboot.template.entity.app.AutoGenStrIdHardDel;

public interface AutoGenStrIdHardDelRepo extends CrudRepository<AutoGenStrIdHardDel, String>, PagingAndSortingRepository<AutoGenStrIdHardDel, String> {

}
