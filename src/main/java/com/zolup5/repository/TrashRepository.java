package com.zolup5.repository;

import com.zolup5.domain.Trash.Trash;
import org.springframework.data.repository.CrudRepository;
public interface TrashRepository extends CrudRepository<Trash, Integer> {
}

