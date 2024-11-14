// declare parameters and assure they are consistent.
{ ->
    runScope = { scope ->
        node {
            checkout scm
            sh("./reg.sh ${scope} OPINIONATED=just_write > scope.groovy")
            scope = load("scope.groovy")
        }
        scope += [failFast: true]
        parallel(scope)
    }
}
