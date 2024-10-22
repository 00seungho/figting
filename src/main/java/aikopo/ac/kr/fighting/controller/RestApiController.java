package aikopo.ac.kr.fighting.controller;

import aikopo.ac.kr.fighting.dto.RestRequestDTO;
import org.springframework.web.bind.annotation.*;

@RestController
public class RestApiController {
    final
        @PostMapping("/send")
        public String sayHello(@RequestBody RestRequestDTO requestDTO) {
            System.out.println(requestDTO.getLatitude());
            System.out.println(requestDTO.getLongitude());
            System.out.println(requestDTO.getPhone_number());
            return "Hello, World!";
    }
}
