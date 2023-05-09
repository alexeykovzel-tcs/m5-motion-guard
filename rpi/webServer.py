from fastapi import FastAPI, HTTPException, File, UploadFile
from fastapi.responses import FileResponse
import helpers
from DBActions import *

from constants import RESET_DB, VIDEO_STORE_PATH

app = FastAPI()


def setupDB():
    try:
        db = DB("database.db")
        db.setup(reset=RESET_DB)
    except Exception as e:
        print(e)


setupDB()

# TODO: needs to be using ssl bc of password/username combo sent over plaintext otherwise
@app.post("/pi/motion")
def postMotion(
    username: str,
    password: str,
    piId: str,
):
    db = DB("database.db")

    # check if username/password combination is valid
    if not helpers.usernamePasswordMatch(db, username, password):
        raise HTTPException(
            status_code=401, detail="Invalid username/password combination"
        )
    print(f"received motion from {username}: piId {piId}")
    db.insertMotion(username, piId)

    return {"message": "Motion detected received by server!"}


@app.get("/user/motion")
def getMotion(
    username: str,
    password: str,
):
    db = DB("database.db")

    # check if username/password combination is valid
    if not helpers.usernamePasswordMatch(db, username, password):
        raise HTTPException(
            status_code=401, detail="Invalid username/password combination"
        )
    print(f"received request for motion from {username}")

    motion = db.getMotion(username)
    return {"motion": motion}


@app.get("/pi/test")
def testConnection(
    username: str,
    password: str,
):
    db = DB("database.db")

    # check if username/password combination is valid
    if not helpers.usernamePasswordMatch(db, username, password):
        raise HTTPException(
            status_code=401, detail="Invalid username/password combination"
        )
    print(f"received test connection from {username}")

    return {"message": "Connection successful!"}


@app.post("/pi/video")
def postVideo(
    username: str,
    password: str,
    piId: str,
    video: UploadFile = File(...),
):
    db = DB("database.db")

    dateTime = time.strftime("%Y-%m-%d_%H-%M-%S")
    # TODO: make this a decorator
    if not helpers.usernamePasswordMatch(db, username, password):
        raise HTTPException(
            status_code=401, detail="Invalid username/password combination"
        )

    store_location = f"{VIDEO_STORE_PATH}/{username}piID{piId}time{dateTime}.avi"
    with open(store_location, "wb") as f:
        f.write(video.file.read())

    # check if username/password combination is valid
    print(f"received video from {username}: piId {piId}")

    db.insertVideo(username, piId, store_location)


@app.get("/pi/videos")
def getVideosList(
    username: str,
    password: str,
):
    db = DB("database.db")

    # check if username/password combination is valid
    if not helpers.usernamePasswordMatch(db, username, password):
        raise HTTPException(
            status_code=401, detail="Invalid username/password combination"
        )
    print(f"received request for videos from {username}")

    videos = db.getVideos(username)
    return videos


@app.get("/pi/video")
def getVideo(
    username: str,
    password: str,
    videoName: str,
):
    db = DB("database.db")

    # check if username/password combination is valid
    if not helpers.usernamePasswordMatch(db, username, password):
        raise HTTPException(
            status_code=401, detail="Invalid username/password combination"
        )
    print(f"received request for video from {username}")

    # check if videoName is from user
    videos = db.getVideos(username)
    if len(videos) == 0:
        raise HTTPException(
            status_code=401, detail="no videos for current user available"
        )
    for video in videos:
        if videoName == video[-1]:
            break
        raise HTTPException(status_code=401, detail="Invalid video name")

    with open(videoName, "rb") as f:
        video = f.read()

    return FileResponse(videoName, media_type="video/avi", filename=videoName)
