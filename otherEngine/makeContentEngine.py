from .decodinglist import decodinglist
import json
import base64

class makeContentEngine:
    def __init__(self,latitude,longitude,image,phoneNumber,writerName=""):
        self._writerName = writerName
        self._title = ""
        self._content = ""
        self._writeName = writerName
        self._latitude = latitude
        self._longitude = longitude
        self._image = f"data:image/jpeg;base64,{base64.b64encode(image).decode('utf-8')}"

        self._phoneNumber = phoneNumber

    def makeTitleandContent(self,address_name, predictions,dataTime):
        print(predictions)
        tag_names = [prediction['tagName'] for prediction in predictions]
        print(tag_names)
        translated_tags = list({decodinglist.get(tag) for tag in tag_names})
        result_string = ", ".join(translated_tags)
        self._title = f"{address_name}근처의 {result_string} 신고합니다."
        self._content = f"{dataTime}경 {address_name}근처에서 {result_string}을 발견했습니다. 빠른 조치 부탁드리겠습니다. 관련 첨부사진은 다음과 같습니다. 감사합니다."

    def to_template(self):
        header = {
            "Content-Type": "application/json"
        }
        body = {
                "title": self._title,
                "content": self._content,
                "image": self._image,
                "latitude": self._latitude,
                "longitude": self._longitude,
                "phoneNumber": self._phoneNumber,
                "writerName": self._writerName
        }
        return header,json.dumps(body)

        


