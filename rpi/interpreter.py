from motion import MyMotionSensor
from emulateSensor import FakeMotionSensor
import time
import threading

from camera import Camera
from serverInteractor import ServerInteractor

from constants import RECORD_TIME, FAKE_MOTION_SENSOR


class Interpreter:
    def __init__(self, username, password, piId) -> None:
        self.username = username
        self.password = password
        self.piId = piId

        self.serverInteractor = ServerInteractor(
            self.username, self.password, self.piId
        )
        if FAKE_MOTION_SENSOR:
            self.motionSensor = FakeMotionSensor(self.serverInteractor.postMotion)
        else:
            self.motionSensor = MyMotionSensor(self.serverInteractor.postMotion)
        self.camera = Camera()
        self.stream()

    def stream(self):
        self.recordingThread = threading.Thread(
            target=self.camera.stream,
            args=(self.serverInteractor.sendImage,),
        )
        self.recordingThread.start()
        return True

    # def main(self): # deprecated
    #     while True:
    #         time.sleep(1)
    #         if self.motionSensor.motion:
    #             self.serverInteractor.postMotion()
    #         # if not self.camera.isRecording:
    #         #     self.startRecording()

    # def startupIfConnectionAvailable(self): # deprecated
    #     try:
    #         if not self.serverInteractor.testConnection():
    #             print("Server sent back a non 200 status code")
    #             self.stop()
    #             exit(1)
    #     except Exception as e:
    #         print(e)
    #         print("server is likely down")
    #         print("shutting down")
    #         self.stop()
    #         exit(1)
    #     print("connection successfull")

    # def uploadVideo(self, videoName): # deprecated
    #     self.serverInteractor.postVideo(videoName)

    def stop(self):
        self.motionSensor.stop()
        self.camera.stop()
        self.recordingThread.join()
        return True
