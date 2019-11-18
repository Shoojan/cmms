package com.sujan.repository;

import com.sujan.bean.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    Member findByFirstName(String firstName);

    List<Member> findAllByOrderByIdDesc();

}
