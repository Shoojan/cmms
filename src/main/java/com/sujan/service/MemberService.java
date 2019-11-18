package com.sujan.service;

import com.sujan.bean.Member;
import com.sujan.service.generic.GenericService;

public interface MemberService extends GenericService<Member> {

	Member findByFirstName(String firstName);

	long getRecentId();
	
}
