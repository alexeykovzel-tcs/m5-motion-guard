import sqlite3
import time


class DB:
    def __init__(self, db_name):
        self.db_name = db_name
        self.conn = sqlite3.connect(self.db_name)
        self.c = self.conn.cursor()

    def setup(self, reset=True):
        self.reset = reset
        if self.reset:
            print("resetting database")
        self.initMotionTable()
        self.initVideoTable()
        self.initUsersTable()
        self.createUser("martijn", "password")
        print("created tables and users")

    def initMotionTable(self):
        if self.reset:
            self.c.execute("DROP TABLE IF EXISTS motion")
        self.c.execute(
            "CREATE TABLE IF NOT EXISTS motion (id INTEGER PRIMARY KEY, timestamp TEXT, username TEXT, piId TEXT)"
        )
        self.conn.commit()

    def initUsersTable(self):
        if self.reset:
            self.c.execute("DROP TABLE IF EXISTS users")
        self.c.execute(
            "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, username TEXT, password TEXT)"
        )
        self.conn.commit()

    def initVideoTable(self):
        if self.reset:
            self.c.execute("DROP TABLE IF EXISTS video")
        self.c.execute(
            "CREATE TABLE IF NOT EXISTS video (id INTEGER PRIMARY KEY, timestamp TEXT, username TEXT, piId TEXT, store_location TEXT)"
        )
        self.conn.commit()

    def createUser(self, username, password):
        # TODO: store hashed password instead of plaintext
        # TODO: check if username already exists
        self.c.execute(
            "INSERT INTO users (username, password) VALUES (?, ?)",
            (username, password),
        )
        self.conn.commit()

    def removeUser(self, username):
        self.c.execute("DELETE FROM users WHERE username=?", (username,))
        self.conn.commit()

    def getUser(self, username):
        self.c.execute("SELECT * FROM users WHERE username=?", (username,))
        user = self.c.fetchone()
        return user

    def insertMotion(self, username, piId):
        # current time in hh:mm:ss format
        timestamp = time.strftime("%H:%M:%S", time.localtime())
        self.c.execute(
            "INSERT INTO motion (timestamp, username, piId) VALUES (?, ?, ?)",
            (timestamp, username, piId),
        )
        self.conn.commit()

    def getMotion(self, username):
        self.c.execute("SELECT * FROM motion WHERE username=?", (username,))
        motions = self.c.fetchall()
        return motions

    def insertVideo(self, username, piId, store_location):
        # current time in hh:mm:ss format
        timestamp = time.strftime("%H:%M:%S", time.localtime())
        self.c.execute(
            "INSERT INTO video (timestamp, username, piId, store_location) VALUES (?, ?, ?, ?)",
            (timestamp, username, piId, store_location),
        )
        self.conn.commit()

    def getVideos(self, username):
        self.c.execute("SELECT * FROM video WHERE username=?", (username,))
        videos = self.c.fetchall()
        return videos

    def stop(self):
        # not used for now, maybe when closing the server this should happen..?
        self.conn.close()
