from serverInteractor import ServerInteractor

from constants import USERNAME, PASSWORD, PI_ID


class Main:
    def __init__(self, username, password, piId):
        self.serverInteractor = ServerInteractor(username, password, piId)
        motion = self.serverInteractor.getMotion()
        print(motion)
        videos = self.serverInteractor.getVideosList()
        print(videos)

        # download the first video
        response = self.serverInteractor.getVideo(videos[0][-1])

        # save the video contained in the FileResponse response
        with open("video.avi", "wb") as f:
            f.write(response.content)

        # write the video to a file
        # with open("clientvideo.avi", "wb") as f:
        #     f.write(video)


if __name__ == "__main__":
    main = Main(USERNAME, PASSWORD, PI_ID)
