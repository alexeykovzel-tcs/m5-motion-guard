from interpreter import Interpreter
import signal
import sys

from constants import USERNAME, PASSWORD, PI_ID

# TODO: pi id needs to also register with a locations
class Main:
    def __init__(self, username, password, piId):
        self.piId = piId
        self.username = username
        self.password = password

        signal.signal(signal.SIGINT, self.signal_handler)

        self.interpreter = Interpreter(self.username, self.password, self.piId)

    def signal_handler(self, sig, frame):
        print("Stopping...")
        self.interpreter.stop()
        sys.exit(0)


if __name__ == "__main__":
    main = Main(USERNAME, PASSWORD, PI_ID)
    # main = Main("1", "123", "1")
