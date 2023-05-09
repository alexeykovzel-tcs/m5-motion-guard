def usernamePasswordMatch(db, username, password):
    user = db.getUser(username)
    if user is None:
        return False
    if user[2] == password:
        return True

    print(user)
    return False
