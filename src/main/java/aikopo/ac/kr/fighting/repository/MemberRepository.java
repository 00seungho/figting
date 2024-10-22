package aikopo.ac.kr.fighting.repository;

import aikopo.ac.kr.fighting.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

}
