package net.ideahut.springboot.template.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface SoftDeleteRepository<T, ID> extends CrudRepository<T, ID>, PagingAndSortingRepository<T, ID> {

	@Override
	@Query("select e from #{#entityName} e where e.deletedOn is null")
	Page<T> findAll(Pageable pageable);

	@Override
	@Query("select e from #{#entityName} e where e.deletedOn is null")
	Optional<T> findById(ID id);
	
}
