package com.zolup5.repository;

import com.zolup5.domain.Trash.Trash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import javax.swing.*;
import java.util.List;
import java.util.Optional;
//@NoRepositoryBean
public interface BookRepository extends JpaRepository<Trash, Integer> {
    Optional <Trash> findByTrashcode(String trashcode);  //되는거임
//    List<Trash> findByTrashcode(String trashcode);
//Trash findByTrashCode(String trashcode);

//    @Query("SELECT m FROM Trash m WHERE m.trashcode = :trashcode")
//    Trash findByTrashCode(@Param("trashcode") String trashcode);
//    Trash findByTrashCode(String trashcode);
}

