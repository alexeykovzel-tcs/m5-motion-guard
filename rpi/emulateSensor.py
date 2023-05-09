import time
import random
import threading


class FakeMotionSensor:
    def __init__(self, postMotionFunction):
        self.postMotionFunction = postMotionFunction

        self.motion = False
        self.run = True

        # run emulatingMotion in a separate thread
        self.emulatingMotionThread = threading.Thread(target=self.emulatingMotion)
        self.emulatingMotionThread.start()

    def emulatingMotion(self):
        # emulates motion by randomly generating a signal that lasts for 5 seconds
        while True:
            print("emulating motion")
            if not self.run:
                break
            # one in 5 chance of motion
            if random.randint(1, 3) == 1:
                print("fake motion detected")
                self.postMotionFunction()
                self.motion = True
                time.sleep(3)
                self.motion = False
            time.sleep(3)

    def getMotion(self):
        return self.motion

    def stop(self):
        self.run = False
        self.emulatingMotionThread.join()
