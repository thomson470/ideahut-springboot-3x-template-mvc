package net.ideahut.springboot.template.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import net.ideahut.springboot.template.entity.EmbeddedHardDel;
import net.ideahut.springboot.template.entity.EmbededId;

public interface EmbeddedHardDelRepo extends CrudRepository<EmbeddedHardDel, EmbededId>, PagingAndSortingRepository<EmbeddedHardDel, EmbededId> {

}
