# from serverInteractor import ServerInteractor
# from interpreter import Interpreter

# interactor = ServerInteractor("1", "123", "1")

# take image form web-cam
# convert image to base64
# send image to server

# import cv2
# import base64

# cap = cv2.VideoCapture(0)
# image = cap.read()[1]
# image = cv2.resize(image, (240, 180))
# image = cv2.imencode(".jpeg", image)[1]

# with open("image.jpeg", "wb") as f:
#     f.write(image)
# image = base64.b64encode(image)
# image = image.decode("utf-8")

# response = interactor.sendImage(image)
# print(response)


# interpreter = Interpreter("1", "123", "1")


# from gpiozero import MotionSensor
# from signal import pause

# motion_sensor = MotionSensor(4)


# def motion():
#     print("Motion detected")


# def no_motion():
#     print("Motion stopped")


# print("Readying sensor...")
# motion_sensor.wait_for_no_motion()
# print("Sensor ready")

# motion_sensor.when_motion = motion
# motion_sensor.when_no_motion = no_motion

# pause()


from serverInteractor import ServerInteractor

serverInteractor = ServerInteractor("1", "invalidpassword", "1")


# test if connection can be made with server with this interpreter
print(serverInteractor.getToken())

# test if motion can be posted # this one does not pass yet because the server responds with 200
print(serverInteractor.postMotion())
