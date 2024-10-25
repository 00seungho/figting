import requests
from dotenv import load_dotenv
import os
import pprint
from PIL import Image, ImageDraw,ImageFont
import io
from .decodinglist import decodinglist
class ObjectDetectionEngine():
    def __init__(self):
        load_dotenv()
        self._TARGET_URL = os.getenv("endpoint")
        self._mskey = os.getenv("mskey")
        self._decodinglist = decodinglist
        self._headers = {
            "Prediction-Key":self._mskey,
            "Content-Type":"application/octet-stream"
        }

    def compress_image_to_target_size(self,byte_image, target_size_mb=3.5):
        # MB를 바이트로 변환 (1MB = 1024 * 1024 바이트)
        target_size_bytes = target_size_mb * 1024 * 1024

        # 이미지 열기
        img = Image.open(io.BytesIO(byte_image))
        quality = 100
        step = 5  # 품질을 낮출 때 사용할 단계

        # 파일 크기를 줄이기 위한 반복
        quality = 95
        step = 5  # 품질을 낮출 때 사용할 단계

        # 파일 크기를 줄이기 위한 반복
        while True:
            # 메모리 상의 버퍼 생성
            buffer = io.BytesIO()

            # 이미지를 메모리 버퍼에 저장하여 파일 크기 확인
            img.save(buffer, 'JPEG', quality=quality)
            output_size = buffer.tell()  # 버퍼의 현재 크기 확인

            # 파일 크기가 목표 이하이면 종료
            if output_size <= target_size_bytes or quality <= 10:
                break

            # 품질을 단계별로 줄임
            quality -= step
        return buffer.getvalue()

    def post_image(self,byte_image):
        response = requests.post(self._TARGET_URL, headers=self._headers, data=byte_image).json()
        try:
            predictions = response.get("predictions", [])
            image = Image.open(io.BytesIO(byte_image))
            filtered_predictions = [pred for pred in predictions if pred.get("probability", 0) >= 0.80]
            for filtered_prediction in filtered_predictions:
                boundingBox = filtered_prediction["boundingBox"]
                image_width, image_height = image.size
                left = int(boundingBox['left'] * image_width)
                top = int(boundingBox['top'] * image_height)
                width = int(boundingBox['width'] * image_width)
                height = int(boundingBox['height'] * image_height)
                right = left + width
                bottom = top + height
                draw = ImageDraw.Draw(image)

                tag_name = filtered_prediction["tagName"]
                probability = filtered_prediction["probability"]
                base_dir = os.path.dirname(os.path.abspath(__file__))
                font_path = os.path.join(base_dir, '..', 'font', 'NanumGothicBold.ttf')
                font_size = 50  # 원하는 글씨 크기로 조정
                font = ImageFont.truetype(font_path, font_size)  # 지정된 크기의 폰트 로드

                text = f"{self._decodinglist[tag_name]} {'정확도 ' + f'{probability * 100:.2f}%'}"
                print(f"감지된 객체: {self._decodinglist[tag_name]}",flush=True)
                print(f"정확도: f'{probability * 100:.2f}%",flush=True)
                text_position = (left, top - font_size - 5)  # 바운딩 박스의 위쪽에 위치
                draw.text(text_position, text, fill="blue", font=font)  # 텍스트 색상은 파란색

                draw.rectangle([left, top, right, bottom], outline="red", width=2)  # 빨간색 박스
            byte_io = io.BytesIO()
            image.save(byte_io, format='JPEG')
            byte_io.seek(0)
            return {
                "image": byte_io.getvalue(),
                "predictions": filtered_predictions
                    }
        except Exception as e:
            return {"error":"ObjectEngine error", "msg":f"Please ask the administrator. {e}"}
