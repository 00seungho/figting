package aikopo.ac.kr.fighting.controller;


import aikopo.ac.kr.fighting.dto.BoardDTO;
import aikopo.ac.kr.fighting.dto.PageRequestDTO;
import aikopo.ac.kr.fighting.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/fighting/")
@RequiredArgsConstructor
@Log4j2
public class FightingController {
    private final BoardService boardService;

    @GetMapping("/")
    public String fightingMain(){

        return "redirect:/fighting/list";
    }



    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){
        model.addAttribute("result", boardService.getList(pageRequestDTO));
    }

    @GetMapping("/register")
    public void register(){

    }

    @PostMapping("/register")
    public String registerPost(BoardDTO dto, RedirectAttributes redirectAttributes){
        BoardDTO Boarddto = BoardDTO.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writerPhone(dto.getWriterPhone())
                .build();
        Long bno = boardService.register(Boarddto);
        redirectAttributes.addFlashAttribute("msg", bno);
        return "redirect:/fighting/list";
    }
    @GetMapping("/read")
    public void read(Long bno, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model){
        BoardDTO dto = boardService.get(bno);
        model.addAttribute("dto", dto);
    }

    @PostMapping("/modify")
    public String modify(BoardDTO dto, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, RedirectAttributes redirectAttributes){
        log.info(dto);
        boardService.modify(dto);

        redirectAttributes.addAttribute("page", requestDTO.getPage());
        redirectAttributes.addAttribute("bno", dto.getBno());
        redirectAttributes.addAttribute("type", requestDTO.getType());
        redirectAttributes.addAttribute("keyword", requestDTO.getKeyword());

        return "redirect:/fighting/read";
    }

    @PostMapping("/remove")
    public String remove(Long bno, RedirectAttributes redirectAttributes){
        boardService.remove(bno);
        redirectAttributes.addFlashAttribute("msg",bno);
        return "redirect:/fighting/list";
    }
}
