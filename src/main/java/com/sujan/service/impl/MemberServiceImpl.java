package com.sujan.service.impl;

import com.sujan.bean.Member;
import com.sujan.repository.MemberRepository;
import com.sujan.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public Member save(Member entity) {
        return memberRepository.save(entity);
    }

    @Override
    public Member update(Member entity) {
        return memberRepository.save(entity);
    }

    @Override
    public void delete(Member entity) {
        memberRepository.delete(entity);
    }

    @Override
    public void delete(Long id) {
        memberRepository.delete(id);
    }

    @Override
    public Member find(Long id) {
        return memberRepository.findOne(id);
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    public Member findByFirstName(String firstName) {
        return memberRepository.findByFirstName(firstName);
    }

    @Override
    public long getRecentId() {
        List<Member> membersInDesc = memberRepository.findAllByOrderByIdDesc();
        if (membersInDesc.isEmpty())
            return 0;
        return membersInDesc.get(0).getId();
    }

    @Override
    public void deleteInBatch(List<Member> members) {
        memberRepository.deleteInBatch(members);
    }

}
