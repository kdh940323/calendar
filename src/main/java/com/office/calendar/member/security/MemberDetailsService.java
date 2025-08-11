package com.office.calendar.member.security;

import com.office.calendar.member.jpa.MemberEntity;
import com.office.calendar.member.jpa.MemberRepository;
import com.office.calendar.member.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class MemberDetailsService implements UserDetailsService {

    final private MemberRepository memberRepository;

    @Autowired
    public MemberDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // username을 받아줌(유저의 아이디)
       log.info("loadUserByUsername()");

        Optional<MemberEntity> optionalMember = memberRepository.findByMemId(username);
        if(optionalMember.isPresent()) {
           MemberEntity findedMemberEntity = optionalMember.get();

           // 유저 인증
           return User.builder()
                    .username(findedMemberEntity.getMemId()) // spring security 인증
                    .password(findedMemberEntity.getMemPw()) // spring security 인증
                    .roles(findedMemberEntity.getAuthorityEntity().getAuthRoleName()) // 조인했을 경우 설정법
//                    .roles("PRE_USER")// 권한 설정(인가)
                    .build();

        }

        return null;
    }

}
