import unittest
from camera import Camera
from interpreter import Interpreter
from serverInteractor import ServerInteractor


class Test_Valid_Interpreter(unittest.TestCase):
    def setUp(self):
        self.interpreter = Interpreter("1", "123", "1")

    def testValidInterpreter(self):
        self.assertEqual(self.interpreter.username, "1")
        self.assertEqual(self.interpreter.password, "123")
        self.assertEqual(self.interpreter.piId, "1")

        # test if connection can be made with server with this self.interpreter
        self.assertTrue(self.interpreter.serverInteractor.getToken())

        # test if self.interpreter can exit properly
        self.assertTrue(self.interpreter.stop())

    def testStreamFunction(self):
        self.assertTrue(self.interpreter.stream())


class Test_Invalid_Interpreter(unittest.TestCase):
    def setUp(self):
        self.interpreter = Interpreter("1", "invalidpassword", "1")

    def testInvalidInterpreter(self):
        self.assertEqual(self.interpreter.username, "1")
        self.assertEqual(self.interpreter.password, "invalidpassword")
        self.assertEqual(self.interpreter.piId, "1")

        # test if connection can be made with server with this interpreter
        self.assertFalse(self.interpreter.serverInteractor.getToken())

        # test if interpreter can exit properly
        self.assertTrue(self.interpreter.stop())


class Test_Valid_ServerInteractor(unittest.TestCase):
    def setUp(self):
        self.serverInteractor = ServerInteractor("1", "123", "1")

    def testValidServerInteractor(self):
        self.assertEqual(self.serverInteractor.username, "1")
        self.assertEqual(self.serverInteractor.password, "123")
        self.assertEqual(self.serverInteractor.piId, "1")

        # test if connection can be made with server with this interpreter
        # self.assertTrue(self.serverInteractor.getToken())

        # test if motion can be posted
        self.assertTrue(self.serverInteractor.postMotion())


class Test_Invalid_ServerInteractor(unittest.TestCase):
    def setUp(self):
        self.serverInteractor = ServerInteractor("1", "invalidpassword", "1")

    def testInvalidServerInteractor(self):
        self.assertEqual(self.serverInteractor.username, "1")
        self.assertEqual(self.serverInteractor.password, "invalidpassword")
        self.assertEqual(self.serverInteractor.piId, "1")

        # test if connection can be made with server with this interpreter
        self.assertFalse(self.serverInteractor.getToken())

        # test if motion can be posted # TODO: this one does not pass yet because the server responds with 200
        self.assertFalse(self.serverInteractor.postMotion())


class Test_Camera(unittest.TestCase):
    def setUp(self):
        self.interpreter = Interpreter("1", "123", "1")

    def testStopFunction(self):
        self.assertTrue(self.interpreter.camera.stop())


