import requests
from dotenv import load_dotenv
import os
import pprint
from otherEngine import kakaoSearchEngine
# load_dotenv()
# TARGET_URL = os.getenv("endpoint")
# mskey = os.getenv("mskey")
# kakaokey = os.getenv("kakaokey")
# kakaourl = "https://dapi.kakao.com/v2/local/geo/coord2address.JSON"
#
latitude = "37.5413542"
longtitude = "126.9693686"
# headers = {
#     "Authorization":"KakaoAK "+kakaokey
# }
# print(kakaokey)
# responese = requests.get(kakaourl+f"?x={longtitude}&y={latitude}",headers=headers)
#
# pprint.pprint(responese.json())

kakaoSearch = kakaoSearchEngine.kakaoMap()
response = kakaoSearch.search(longitude=longtitude,latitude=latitude)
pprint.pprint(response)

