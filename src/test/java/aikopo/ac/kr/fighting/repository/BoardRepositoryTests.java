package aikopo.ac.kr.fighting.repository;

import aikopo.ac.kr.fighting.entity.Board;
import aikopo.ac.kr.fighting.entity.Member;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

@SpringBootTest
public class BoardRepositoryTests {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PageableHandlerMethodArgumentResolver pageableResolver;
    @Test
    public void inseruser(){
        IntStream.rangeClosed(1,100).forEach(i -> {
            Member member= Member.builder()
                    .phoneNum("010-"+i)
                    .name("user"+i)
                    .build();
            memberRepository.save(member);
        });
    }

    @Test
    public void inserBorads(){
        Random random = new Random();

        List<Member> members =  memberRepository.findAll();
        IntStream.rangeClosed(1,100).forEach(i -> {
            Boolean bool = random.nextBoolean();
            Member randomMember = members.get(random.nextInt(members.size())); // 랜덤한 Member 선택
            Board board = Board.builder()
                    .title("Title"+i)
                    .content("Content"+i)
                    .writer(randomMember)
                    .processed(bool)
                    .build();
            boardRepository.save(board);
        });

    }

    @Transactional
    @Test
    public void testRead(){
        Optional<Board> result = boardRepository.findById(5L);
        Board board = result.get();
        System.out.println(board);
        System.out.println(board.getWriter());
    }


    @Test
    public void testRead3(){
        Object result = boardRepository.getBoardByBno(99L);
        Object[] arr = (Object[]) result;
        System.out.println(Arrays.toString(arr));
    }

    @Test
    public void testSearch1(){
        boardRepository.search1();
    }
    @Test
    public void testSearchPage(){
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());
        boardRepository.searchPage("t","1",pageable);
    }
}
