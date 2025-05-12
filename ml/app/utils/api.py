
def response_ok(message):
    return {"message": message}, 200


def response_bad_req(message):
    return {"message": message}, 400