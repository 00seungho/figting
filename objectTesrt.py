import requests
from dotenv import load_dotenv
import os
import pprint
from PIL import Image, ImageDraw,ImageFont
import io
load_dotenv()
TARGET_URL = os.getenv("endpoint")
mskey = os.getenv("mskey")

decodinglist = {
    "grate_inlet_block" : "빗물받이 막힘",
    "open_grate_inlet" : "빗물받이 열림",
    "paving_block_break": "보도블럭 파손",
    "port_hole": "포트홀",
    "sinkhole": "싱크홀"
}

headers = {
    "Prediction-Key":mskey,
    "Content-Type":"application/octet-stream"
}

with open('싱크홀_2_1.jpg', 'rb') as f:
    file = f.read()
body = file

response = requests.post(TARGET_URL,headers=headers,data=body).json()
predictions = response.get("predictions", [])

image = Image.open(io.BytesIO(file))

filtered_predictions = [pred for pred in predictions if pred.get("probability", 0) >= 0.15]
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

    font_size = 30  # 원하는 글씨 크기로 조정
    font_path = 'font/NanumGothicBold.ttf'  # 실제 TTF 폰트 파일 경로로 변경하세요.
    font = ImageFont.truetype(font_path, font_size)  # 지정된 크기의 폰트 로드

    text = f"{decodinglist[tag_name]} {'정확도 ' + f'{probability * 100:.2f}%'}"
    text_position = (left, top - font_size - 5)  # 바운딩 박스의 위쪽에 위치
    draw.text(text_position, text, fill="blue",font=font)  # 텍스트 색상은 파란색

    draw.rectangle([left, top, right, bottom], outline="red", width=2)  # 빨간색 박스

image.show()
byte_io = io.BytesIO()
image.save(byte_io, format='JPEG')
byte_data = byte_io.getvalue()


pprint.pprint(filtered_predictions)