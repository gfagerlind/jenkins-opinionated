// boilerplate to get jenkins started
_main = []
def call(file) {
    def _import_file = {
        obj = load("${pwd()}/${file}")
        if (obj instanceof java.util.LinkedHashMap) {
            // Most things runs just in init phase,
            // but for a cleaner log look, we can add a main step,
            // and put things there instead.
            if (obj.main) {
                _main += obj.main
                obj.main.delegate = this
            }
            if (obj.init) {
                obj.init.delegate = this
                obj.init()
            }

        } else {
            obj.delegate = this
            obj()
        }
    }
    if (! env.NODE_NAME) {
        node {
            checkout scmGit()
            _import_file()
        }
    } else {
        _import_file()
    }
}
return this
