package com.icia.memberboard.dto;

import com.icia.memberboard.entity.MemberEntity;
import com.icia.memberboard.util.UtilClass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class MemberDTO {

    private Long id;
    private String memberEmail;
    private String memberPassword;
    private String memberName;
    private String memberMobile;
    private String memberBirth;
    private String memberProfile;
    private String createdAt;
    private MultipartFile profile;

    public static MemberDTO toMemberDTO(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setMemberEmail(memberEntity.getMemberEmail());
        memberDTO.setMemberPassword(memberEntity.getMemberPassword());
        memberDTO.setMemberName(memberEntity.getMemberName());
        memberDTO.setMemberMobile(memberEntity.getMemberMobile());
        memberDTO.setMemberBirth(memberEntity.getMemberBirth());
        memberDTO.setMemberProfile(memberEntity.getMemberProfile());
        memberDTO.setCreatedAt(UtilClass.dateFormat(memberEntity.getCreatedAt()));
        return memberDTO;
    }
}
