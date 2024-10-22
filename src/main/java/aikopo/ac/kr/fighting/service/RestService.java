package aikopo.ac.kr.fighting.service;

import aikopo.ac.kr.fighting.dto.RestRequestDTO;
import aikopo.ac.kr.fighting.entity.Board;
import aikopo.ac.kr.fighting.entity.Member;

public interface RestService {
    String registerMember(RestRequestDTO requestDTO);
    default Member RestToMember(RestRequestDTO requestDTO){
        Member member = Member.builder()
                .name(requestDTO.getWriterName())
                .phoneNum(requestDTO.getPhone_number())
                .build();
        return member;
    };



}
