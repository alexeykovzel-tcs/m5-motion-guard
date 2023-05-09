import cv2
import base64
import requests

# ensure that the webcam and the server are running
# (refer to README.md)

# url to the server
server_url = 'http://192.168.178.224:8080/pi/1/webcam/'

# open the video file
webcam = cv2.VideoCapture(0)
webcam.set(cv2.CV_CAP_PROP_FPS, 5)

while True:
    # retrieve the next frame as base64
    ret, frame = webcam.read()
    frame = cv2.resize(frame, (640, 480))
    encoded, buffer = cv2.imencode('.jpeg', frame)
    data = base64.b64encode(buffer)
    requests.post(server_url, json={'base64': data})
