import cv2
import time
import base64

from constants import STREAM_FPS, STREAM_QUALITY


class Camera:
    def __init__(self):
        self.vid = cv2.VideoCapture(0)
        self.width = int(self.vid.get(cv2.CAP_PROP_FRAME_WIDTH))
        self.height = int(self.vid.get(cv2.CAP_PROP_FRAME_HEIGHT))
        self.n_videos = 0
        self.isRecording = False
        self.run = True

    def record(self, outputName, record_time, uploadFunction):
        print(f"starting recording of {record_time} seconds")
        self.isRecording = True
        self.n_videos += 1
        writer = cv2.VideoWriter(
            outputName,
            cv2.VideoWriter_fourcc(*"XVID"),
            20,
            (self.width, self.height),
        )
        end_time = time.time() + record_time
        while time.time() < end_time:
            if not self.run:
                break
            ret, frame = self.vid.read()
            writer.write(frame)

        writer.release()
        self.isRecording = False
        uploadFunction(outputName)

    def stream(self, uploadFunction):
        # sends a stream to the web server
        # TODO: fix this
        fps = STREAM_FPS
        while self.run:
            ret, frame = self.vid.read()
            frame = cv2.resize(frame, STREAM_QUALITY)
            _, buffer = cv2.imencode(".jpeg", frame)
            image = base64.b64encode(buffer)
            image = image.decode("utf-8")
            uploadFunction(image)
            time.sleep(1 / fps)
        return True

    def stop(self):
        self.vid.release()
        cv2.destroyAllWindows()
        self.run = False
        return True


if __name__ == "__main__":
    camera = Camera()
    print(camera.startRecording(10))
