package aikopo.ac.kr.fighting.service;

import aikopo.ac.kr.fighting.dto.RestBodyDTO;
import aikopo.ac.kr.fighting.entity.Board;
import aikopo.ac.kr.fighting.entity.Image;
import aikopo.ac.kr.fighting.entity.Member;
import aikopo.ac.kr.fighting.repository.BoardRepository;
import aikopo.ac.kr.fighting.repository.ImageRepository;
import aikopo.ac.kr.fighting.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RestServiceImpl implements RestService{
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final ImageRepository imageRepository;
    @Value("${image.upload.dir}") // 이미지 저장 경로를 application.properties에서 가져옴
    private String uploadDir;
    public String registerMember(RestBodyDTO requestDTO){
        Member member = RestToMember(requestDTO);
        memberRepository.save(member);
        return member.getPhoneNum();
    }

    public void registerBoard(RestBodyDTO restbodyDTO, String phoneNumber){

        String[] parts = restbodyDTO.getImage().split(",");
        String imageData = parts[1];
        byte[] imageBytes = Base64.getDecoder().decode(imageData);
        Image tempImage = new Image();
        imageRepository.save(tempImage);
        String fileName = "image_" + tempImage.getId() + ".jpg";
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            System.out.println(uploadDirectory.mkdirs()); // 폴더 생성
        }

        // 파일을 업로드 디렉토리에 저장
        File destinationFile = new File(uploadDir, fileName);
        String relativePath = "uploads/" + fileName;
        try (FileOutputStream fos = new FileOutputStream(destinationFile)) {
            fos.write(imageBytes); // 디코딩된 이미지를 파일로 저장
        } catch (IOException e) {
            e.printStackTrace(); // 예외 처리: 파일 저장 중 문제가 발생하면 스택 트레이스 출력
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
        Member member = memberRepository.getById(phoneNumber);

        Board board = Board.builder()
                .title(restbodyDTO.getTitle())
                .content(restbodyDTO.getContent())
                .processed(false)
                .picPath(relativePath)
                .writer(member)
                .build();


        boardRepository.save(board);
    }



}
