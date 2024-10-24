package aikopo.ac.kr.fighting.dto;

import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestBodyDTO {
    private String title;
    private String content;
    private String image;
    private String latitude;
    private String longitude;
    private String phoneNumber;
    private String writerName;
}
