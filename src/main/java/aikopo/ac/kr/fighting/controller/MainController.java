package aikopo.ac.kr.fighting.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Log4j2
public class MainController {
    @GetMapping("/")
    public String fightingMain(){

        return "redirect:/fighting/list";
    }

}
