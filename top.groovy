// top jenkins file, without boilerplate
{ ->
    import_file('./libs/DeclareParameter.groovy')
    withCredentials([usernamePassword(credentialsId: 'test_user_id',
                                      usernameVariable: 'USERNAME',
                                      passwordVariable: 'PASSWORD')]) {
        sh '''#!/usr/bin/bash
            echo "$USERNAME $PASSWORD (Jenkins will try to not leak your passwords, but we can force it: ${PASSWORD^^})"
        '''
    }
    withParameters()
    {
        // Typically this job is triggered by some scm event, and will
        // decide on what to do depending of the event type.
        // Or what ever you want to do...
        declareParameter(string(name: 'JOB_TYPE', defaultValue: ''))
        if (params.JOB_TYPE == "d") {
            import_file("dep.groovy")
        }
        if (params.JOB_TYPE == 'p') {
            import_file("postmerge.groovy")
        }
        if (params.JOB_TYPE == 'c') {
            import_file("check.groovy")
        }
    }
    // What about not dependent gate? - that is trivial, just remove the dependentGate closure,
    // What if i want to do wacky combinations of stageWithWip and dependentGate? - go right ahead, but i dont support dependentGate in dependentGate because i dont know what it would mean
}
