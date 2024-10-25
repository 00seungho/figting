package aikopo.ac.kr.fighting.dto;

import lombok.*;

import java.time.LocalDateTime;


@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
    private Long bno;
    private String title;
    private String content;
    private String writerPhone;
    private String writerName;
    private String picPath;
    private Boolean processed;
    private String longitude;
    private String latitude;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

}
