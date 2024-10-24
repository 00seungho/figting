import json
import pprint
from flask import Flask, request, Response,jsonify
import requests
import os
from dotenv import load_dotenv
from otherEngine import kakaoSearchEngine
from otherEngine import objectDetectionEngine
from otherEngine import makeContentEngine
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # 모든 도메인에서 접근을 허용
load_dotenv()
# 클라이언트 요청을 전달할 타겟 서버 URL
TARGET_URL = os.getenv("endpoint")
mskey = os.getenv("mskey")
KakaoSearchEngine = kakaoSearchEngine.kakaoMap()
ObjectDetectionEngine = objectDetectionEngine.ObjectDetectionEngine()
backurl = os.getenv("backendpoint")

@app.route('/send', methods=['POST'])
def proxy():
    if request.content_type != 'application/octet-stream':
        print("컨텐츠타입 x")
        return jsonify({'error': 'Content-Type must be octet-stream'}), 400
    else:
        img = request.get_data()
        if img is None:
            print("이미지 못띄움")
            return jsonify({'error': 'no image in body or it is an invalid value'}), 400
        else:
            longitude = request.headers.get("longitude")
            latitude = request.headers.get("latitude")
            phoneNumber = request.headers.get("phoneNumber")
            dataTime = request.headers.get("dateTime")

            # print(longitude)
            # print(latitude)
            map = KakaoSearchEngine.search(longitude=longitude,latitude=latitude)
            byteimg = request.get_data()
            byteimg_resize = ObjectDetectionEngine.compress_image_to_target_size(byteimg)
            prediction = ObjectDetectionEngine.post_image(byteimg_resize)
            if (len(prediction["predictions"]) == 0):
                return jsonify({"notice":"사용자의 사진에서 민원내용을 찾을 수 없습니다."},200)
            print(map)
            template = makeContentEngine.makeContentEngine(latitude=latitude,
                                                           longitude=longitude,
                                                           phoneNumber=phoneNumber,
                                                           image=prediction["image"])
            template.makeTitleandContent(address_name=map["address_name"],predictions=prediction["predictions"],dataTime=dataTime)
            template_header = template.to_template()[0]
            template_body = template.to_template()[1]
            requests.post(backurl,headers=template_header,data=template_body)
    return "done!"
if __name__ == '__main__':
    app.run("0.0.0.0",port=8080,debug=True)
