from gpiozero import MotionSensor
import threading
import time

from constants import TIME_OUT_TIME


class MyMotionSensor:
    def __init__(self, postMotionFunction):
        self.postMotionFunction = postMotionFunction

        self.run = True
        self.detectMotionThread = threading.Thread(target=self.detectMotion)
        self.detectMotionThread.start()

        self.deactiveTime = 0  # time at which motion is set to false, initialized to 0

    def detectMotion(self):
        # uses the motion sensor of the pi to detect motion
        motion_sensor = MotionSensor(4)
        motion_sensor.when_motion = self.motionDetected
        while True:
            time.sleep(1)
            if time.time() > self.deactiveTime:
                self.motion = False
            if not self.run:
                break

    def motionDetected(self):
        # if motion is detected within the timeouttime, nothing happens.
        # else, postmotionfunction is called which sends a motion detection to the server
        # and the deactivetime is set to the current time + timeouttime
        if time.time() < self.deactiveTime:
            print("motion detected within timeout time")
            return
        print("motion detected")

        print("motion response", self.postMotionFunction())
        self.deactiveTime = time.time() + TIME_OUT_TIME

    def stop(self):
        self.run = False
        self.detectMotionThread.join()
