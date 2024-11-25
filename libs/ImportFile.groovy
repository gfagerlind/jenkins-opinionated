// boilerplate to get jenkins started
def call(file) {
    obj = load("${pwd()}/${file}")
    obj.delegate = this
    return obj()
}
return this
