package com.zolup5.repository;

import com.zolup5.domain.user.Member;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface AccountsRepository extends CrudRepository<Member,Long> {
//    @Override
//    ArrayList<Accounts> findAll();
    List<Member> findByArea(String area);

//    List<Member> findByMember(String name);
}
