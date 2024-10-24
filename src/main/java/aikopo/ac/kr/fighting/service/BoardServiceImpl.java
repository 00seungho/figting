package aikopo.ac.kr.fighting.service;

import aikopo.ac.kr.fighting.dto.BoardDTO;
import aikopo.ac.kr.fighting.dto.PageRequestDTO;
import aikopo.ac.kr.fighting.dto.PageResultDTO;
import aikopo.ac.kr.fighting.entity.Board;
import aikopo.ac.kr.fighting.entity.Member;
import aikopo.ac.kr.fighting.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{
    private final BoardRepository repository;

    @Override
    public Long register(BoardDTO dto) {
        Board board = dtoToEntity(dto);
        repository.save(board);
        return board.getBno();
    }

    @Override
    public PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO) {

        Function<Object[], BoardDTO> fn = (en -> entityToDTO((Board) en[0], (Member) en[1]));

        Page<Object[]> result = repository.searchPage(pageRequestDTO.getType(), pageRequestDTO.getKeyword(),
                pageRequestDTO.getPageable(Sort.by("bno").descending()));


        return new PageResultDTO<>(result, fn);
    }

    @Override
    public void remove(Long bno){
        repository.deleteById(bno);
    };

    @Override
    public BoardDTO get(Long bno) {
        Board result = repository.getBoardByBno(bno);
        BoardDTO boardDTO = entityToDTO(result,result.getWriter());
        return boardDTO;
    }

    @Transactional
    @Override
    public void modify(BoardDTO dto) {
        Board board = repository.getReferenceById(dto.getBno());
        board.changeProcessed(dto.getProcessed());
        repository.save(board);
    }
}