import requests

from constants import DEVELOPMENT, SERVER_IP
import time


class ServerInteractor:
    def __init__(self, username, password, piId):
        self.ip = SERVER_IP
        self.piId = piId
        self.username = username
        self.password = password

        self.session = requests.Session()
        self.getToken()

        self.imageCounter = 0

    def getToken(self):
        self.session.post(
            f"https://motionguard.venin.ga/perform-login?username={self.username}&password={self.password}"
        )
        try:
            self.token = self.session.cookies.get_dict()["JSESSIONID"]
            print(self.token)
            return True
        except KeyError:
            self.token = None
            return False

    def postMotion(self):
        print("posting motion")
        r = requests.post(
            self.ip + "/pi/motion",
            headers={
                "PI_ID": self.piId,
            },
            cookies={"JSESSIONID": self.token},
        )
        print(r)
        if r.status_code == 200:
            return True
        return False

    def sendImage(self, image):  # sends an image in base64 format
        if DEVELOPMENT:
            r = requests.post(
                self.ip + f"/pi/frames?id={self.piId}",
                json={"base64": image},
                cookies={"JSESSIONID": self.token},
            )
        else:
            r = requests.post(
                self.ip + f"/pi/frames?id={self.piId}",
                json={"base64": image},
                cookies={"JSESSIONID": self.token},
            )
            if r.status_code == 200:
                return True
            return False
