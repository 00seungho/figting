package aikopo.ac.kr.fighting.repository;

import aikopo.ac.kr.fighting.entity.Board;
import aikopo.ac.kr.fighting.repository.search.SearchBoardRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board,Long> , SearchBoardRepository {
    // Fetch Board entity by bno
    @Query("SELECT b FROM Board b WHERE b.bno = :bno")
    Board getBoardByBno(@Param("bno") Long bno);

}
