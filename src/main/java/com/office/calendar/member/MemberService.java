package com.office.calendar.member;


import com.office.calendar.member.jpa.MemberEntity;
import com.office.calendar.member.jpa.MemberRepository;
import com.office.calendar.member.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class MemberService {

    final private static byte USER_ID_ALREADY_EXIST = 0;
    final private static byte USER_SIGNUP_SUCCESS = 1;
    final private static byte USER_SIGNUP_FAIL = -1;

    final private static byte MODIFY_SUCCESS    = 1;
    final private static byte MODIFY_FAIL       = 0;

    final private static byte NEW_PASSWORD_CREATION_SUCCESS = 1;
    final private static byte NEW_PASSWORD_CREATION_FAIL = 0;

    final private PasswordEncoder passwordEncoder;

    final private JavaMailSender javaMailSender;

//    final private MemberMapper memberMapper;

    final private MemberRepository memberRepository;

    public MemberService(PasswordEncoder passwordEncoder,
                         JavaMailSender javaMailSender,
                         MemberMapper memberMapper,
                         MemberRepository memberRepository) {

        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
//        this.memberMapper = memberMapper;
        this.memberRepository = memberRepository;
    }

    public int signupConfirm(MemberDto memberDto) {

//        boolean isMember = memberDao.isMember(memberDto.getId());
//        boolean isMember = memberMapper.isMember(memberDto.getId());
        boolean isMember = memberRepository.existsByMemId(memberDto.getId());
        log.info("isMember", isMember);

        if(!isMember) {

            String encodedPW = passwordEncoder.encode(memberDto.getPw());
//            memberDto.setPw(encodedPW); > 좋은 방법은 아님
//
//           int result = memberDao.insertMember(memberDto);
//           int result = memberMapper.insertMember(memberDto, encodedPW);
//           if(result > 0)
//               return USER_SIGNUP_SUCCESS;
//           else
//               return USER_SIGNUP_FAIL;

//        MemberEntity memberEntity = MemberEntity.builder()
//                .memId(memberDto.getId())
//                .memPw(encodedPW)
//                .memMail(memberDto.getMail())
//                .memPhone(memberDto.getPhone())
//                .build();

            MemberEntity memberEntity = memberDto.toEntity();
            memberEntity.setMemPw(encodedPW);

        MemberEntity savedMemberEntity = memberRepository.save(memberEntity);
        if (savedMemberEntity != null) {
            return USER_SIGNUP_SUCCESS;
        } else {
            return USER_SIGNUP_FAIL;
        }

    } else

        {
            return USER_ID_ALREADY_EXIST;
        }

    }

    public String signinConfirm(MemberDto memberDto) {

//        MemberDto dto = memberDao.selectMemberByID(memberDto.getId());
//        MemberDto dto = memberMapper.selectMemberByID(memberDto.getId());
//        if(dto!= null && passwordEncoder.matches(memberDto.getPw(), dto.getPw())) {
//            System.out.println("[MemberService] member login success");
//            return dto.getId();
//
//        } else {
//            System.out.println("[MemberService] member login fail");
//            return null;
//        }

        Optional<MemberEntity> optionalMember =
                memberRepository.findByMemId(memberDto.getId());

        if(optionalMember.isPresent() &&
                passwordEncoder.matches(memberDto.getPw(), optionalMember.get().getMemPw())) {

            log.info("MEMBER LOGIN SUCCESS");

        } else {
            log.info("MEMBER LOGIN FAIL");

        }
        return null;
    }

    public MemberDto modify(String loginedID) {

//        return memberDao.selectMemberByID(loginedID);
//        return memberMapper.selectMemberByID(loginedID);

        Optional<MemberEntity> optionalMember =
                memberRepository.findByMemId(loginedID);
        if(optionalMember.isPresent()) {
            MemberEntity findMemberEntity = optionalMember.get();
//            MemberDto memberDto = MemberDto.builder()
//                    .no(findMemberEntity.getMemNo())
//                    .id(findMemberEntity.getMemId())
//                    .mail(findMemberEntity.getMemMail())
//                    .phone(findMemberEntity.getMemPhone())
//                    .authority_no(findMemberEntity.getMemAuthorityNo())
//                    .reg_date(findMemberEntity.getMemRegDate().toString())
//                    .mod_date(findMemberEntity.getMemModDate().toString())
//                    .build();

            return findMemberEntity.toDto();

        }
            return null;
    }

    public int modifyConfirm(MemberDto memberDto) {

        String encodedPW = passwordEncoder.encode(memberDto.getPw());
//        return memberDao.updateMember(memberDto, encodedPW);
//        return memberMapper.updateMember(memberDto, encodedPW);

        Optional<MemberEntity> optionalMember =
                memberRepository.findById(memberDto.getNo());
        if(optionalMember.isPresent()) {
            MemberEntity findMemberEntity = optionalMember.get();
            findMemberEntity.setMemPw(encodedPW);
            findMemberEntity.setMemMail(memberDto.getMail());
            findMemberEntity.setMemPhone(memberDto.getPhone());

            memberRepository.save(findMemberEntity);

            return MODIFY_SUCCESS;
        }
            return MODIFY_FAIL;
    }

    public int findpasswordConfirm(MemberDto memberDto) {

//        MemberDto selectedMemberDto =
////                memberDao.selectMemberByIdAndMail(memberDto);
//                memberMapper.selectMemberByIdAndMail(memberDto);
//
//        int result = 0;
//        if(selectedMemberDto != null) {
//
//            String newPassword = createNewPassword();
//
////            result = memberDao.updatePassword(memberDto.getId(), passwordEncoder.encode(newPassword));
//            result = memberMapper.updatePassword(memberDto.getId(), passwordEncoder.encode(newPassword));
//            if(result > 0) {
//                sendNewPasswordByMail(memberDto.getMail(), newPassword);
//            }
//
//        }

//        return result;

        Optional<MemberEntity> optionalMember =
                memberRepository.findByMemIdAndMemMail(memberDto.getId(), memberDto.getMail());
        if(optionalMember.isPresent()) {
            String newPassword = createNewPassword();
            MemberEntity findedMemberEntity = optionalMember.get();
            findedMemberEntity.setMemPw(passwordEncoder.encode(newPassword));
            MemberEntity updateMember = memberRepository.save(findedMemberEntity);
            if(updateMember != null) {
                sendNewPasswordByMail(memberDto.getMail(), newPassword);
            }
            return NEW_PASSWORD_CREATION_SUCCESS;
        }

        return NEW_PASSWORD_CREATION_FAIL;

    }

    private String createNewPassword() {

        char[] chars = new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z'
        };

        StringBuffer stringBuffer = new StringBuffer();
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(new Date().getTime());

        int index = 0;
        int length = chars.length;
        for (int i = 0; i < 8; i++) {
            index = secureRandom.nextInt(length);

            if (index % 2 == 0) {
                stringBuffer.append(String.valueOf(chars[index]).toUpperCase());

            } else {
                stringBuffer.append(String.valueOf(chars[index]).toLowerCase());
            }


        }

        return stringBuffer.toString();

    }

    private void sendNewPasswordByMail(String toMailAddr, String newPassword) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo("sun2cya@gmail.com");
        simpleMailMessage.setSubject("[MyCalendar] 새 비밀번호 안내입니다.");
        simpleMailMessage.setText("새 비밀번호: " + newPassword);
        simpleMailMessage.setFrom("sun2cya@gmail.com");

        javaMailSender.send(simpleMailMessage);

    }
}
