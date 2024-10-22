package aikopo.ac.kr.fighting.dto;

import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestRequestDTO {
    private String latitude;
    private String longitude;
    private String image;
    private String phone_number;

    private String title;
    private String content;
    private String writerName;
    private String pic;
    private String detectionPic;
}
