package aikopo.ac.kr.fighting.controller;

import aikopo.ac.kr.fighting.dto.RestBodyDTO;
import aikopo.ac.kr.fighting.service.RestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RestApiController {

    private final RestService restService;

    @PostMapping("/sendBoard")
    public String BoardInsert(@RequestBody RestBodyDTO restBodyDTO) {
        String number = restService.registerMember(restBodyDTO);

        restService.registerBoard(restBodyDTO,number);
        return "save done";
    }
}
