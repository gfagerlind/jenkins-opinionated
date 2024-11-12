stage('init') {
    node() {
        checkout scm
        import_file = load("${pwd()}/libs/ImportFile.groovy")
        import_file('top.groovy')
    }
}
import_file._main.each { it() }
