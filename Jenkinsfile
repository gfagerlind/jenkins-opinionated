checkout_scm = { checkout scmGit(
    branches: [[name: '$GIT_REF']],
    extensions: [],
    userRemoteConfigs: [[
        name: 'origin', refspec: '+refs/heads/*:refs/remotes/origin/*',
        url: '/git/jenkins-opinionated']])
}

stage('init') {
    node() {
        checkout_scm()
        withCredentials([usernamePassword(credentialsId: 'test_user_id',
                                          usernameVariable: 'USERNAME',
                                          passwordVariable: 'PASSWORD')]) {
            sh '''#!/usr/bin/bash
                echo "$USERNAME $PASSWORD (Jenkins will try to not leak your passwords, but we can force it: ${PASSWORD^^})"
            '''
        }
        import_file = load("${pwd()}/libs/ImportFile.groovy")
        import_file('libs/DeclareParameter.groovy')
        import_file('libs/DependentGate.groovy')
        import_file('libs/NodeWithPriority.groovy')
        import_file('libs/Queue.groovy')
        import_file('libs/Reg.groovy')
        import_file('libs/StageWithWip.groovy')
        import_file('libs/TypicalJob.groovy')
        import_file('top.groovy')
    }
    withParameters()
    {
        // Typically this job is triggered by some scm event, and will
        // decide on what to do depending of the event type.
        // Or what ever you want to do...
        declareParameter(string(name: 'SCOPE', defaultValue: 'check', description: ""))
        declareParameter(string(name: 'GIT_REF', defaultValue: 'origin/master'))
        currentBuild.description = "${params.SCOPE}@${params.GIT_REF}"
    }
}
top()
