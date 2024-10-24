package aikopo.ac.kr.fighting.service;

import aikopo.ac.kr.fighting.dto.RestBodyDTO;
import aikopo.ac.kr.fighting.entity.Member;

public interface RestService {
    String registerMember(RestBodyDTO requestDTO);
    void registerBoard(RestBodyDTO requestDTO, String phoneNumber);
    default Member RestToMember(RestBodyDTO requestDTO){
        Member member = Member.builder()
                .name(requestDTO.getWriterName())
                .phoneNum(requestDTO.getPhoneNumber())
                .build();
        return member;
    };



}
