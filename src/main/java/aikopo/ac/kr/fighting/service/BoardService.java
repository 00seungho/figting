package aikopo.ac.kr.fighting.service;

import aikopo.ac.kr.fighting.dto.BoardDTO;
import aikopo.ac.kr.fighting.dto.PageRequestDTO;
import aikopo.ac.kr.fighting.dto.PageResultDTO;
import aikopo.ac.kr.fighting.entity.Board;
import aikopo.ac.kr.fighting.entity.Member;

public interface BoardService {
    //    새글을 등록하는 기능
    Long register(BoardDTO dto);
    //    게시목록 처리 기능
    PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO);
    //   특정 게시글 하나를 조회하는 기능
    BoardDTO get(Long bno);
    //    수정 기능

    public void remove(Long bno);

    void modify(BoardDTO boardDTO);

    //    Entity를 DTO로 변환하는 메소드
    default BoardDTO entityToDTO(Board board, Member member){
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .writerPhone(member.getPhoneNum())
                .writerName(member.getName())
                .processed(board.getProcessed())
                .picPath(board.getPicPath())
                .build();

        return boardDTO;
    }

    //    DTO를 Entity로 변환하는 메소드(
    default Board dtoToEntity(BoardDTO dto){
        Member member = Member.builder()
                .phoneNum(dto.getWriterPhone())
                .name(dto.getWriterName())
                .build();

        Board board=Board.builder()
                .bno(dto.getBno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(member)
                .processed(dto.getProcessed())
                .build();
        return board;
    }
}