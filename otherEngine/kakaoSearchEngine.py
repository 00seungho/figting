import requests
from dotenv import load_dotenv
import os

class kakaoMap():
    def __init__(self):
        load_dotenv()
        self._kakaokey = "KakaoAK "+os.getenv("kakaokey")
        self._kakaourl = "https://dapi.kakao.com/v2/local/geo/coord2address.JSON"

    def search(self,longitude,latitude):
        headers = {
            "Authorization": self._kakaokey
        }
        url = self._kakaourl +f"?x={longitude}&y={latitude}"
        responese = requests.get(url, headers=headers).json()
        if "errorType" in responese:
            return {"error":"kakao api requests error", "msg":responese["message"]}
        else:
            doc = responese["documents"]
            road_address = doc[0]["road_address"]
            address_name = road_address["address_name"]
            region_1depth_name = road_address["region_1depth_name"]
            region_2depth_name = road_address["region_2depth_name"]
            region_3depth_name = road_address["region_3depth_name"]
            print(f"현재 위치: {address_name}")
        return {"address_name":address_name,"region_1depth_name":region_1depth_name,"region_2depth_name":region_2depth_name,"region_3depth_name":region_3depth_name}