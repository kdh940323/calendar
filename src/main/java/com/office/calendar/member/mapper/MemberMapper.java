package com.office.calendar.member.mapper;

import com.office.calendar.member.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface MemberMapper {

    public boolean isMember(String id);

    public int insertMember(@Param("memberDto") MemberDto memberDto, @Param("encodedPW") String encodedPW);

    public MemberDto selectMemberByID (String id);

    public int updateMember(@Param("memberDto") MemberDto memberDto, @Param("encodedPW") String encodedPW);

    public MemberDto selectMemberByIdAndMail(MemberDto memberDto);

    public int updatePassword(String id, String encode);


}
