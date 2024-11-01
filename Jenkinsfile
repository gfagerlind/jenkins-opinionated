// boilerplate to get jenkins started
_main = []
def import_file(file) {
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
            checkout scm
            _import_file()
        }
    } else {
        _import_file()
    }
}
scopes = []
stage('init') {
    import_file('./libs/DeclareParameter.groovy')
    withParameters()
    {
        import_file('top.groovy')
    }
}
_main.each { it() }
