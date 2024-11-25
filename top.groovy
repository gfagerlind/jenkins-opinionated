// top jenkins file, without boilerplate
{ ->
    top = {
        // What if i want to do wacky combinations of stageWithWip and dependentGate?
        // - go right ahead, but i dont support dependentGate in dependentGate because i dont know what it would mean
        if (params.SCOPE == "gate") {
            dependentGate(4, {
                runScope(params.SCOPE)
            },
            {
                // TODO: the merge activities
                // This is quite dependent on how much you trust the system
                // I would install heavy guard rails here, because thats how I am
                // which would mean that you would most likely have a remote
                // speculation branch - like candidates/master
                // each gate job would then add their candidates there,
                // then each job just check if there is a previous gate job running,
                // if so, check what version it is speculating on,
                // speculate on top of that one,
                // run the tests,
                // wait for the depedent job to finish
                // check the speculating parent is indeed the new master
                // fast forwarded
                //
                // But you can also trust that the system does it right and just merge
                node {
                    sh('git fetch origin')
                    sh('git checkout origin/test/master')
                    sh("git merge ${params.GIT_REF}")
                    sh('git push origin HEAD:test/master')
                }
            }
            )
        }
        if (params.SCOPE == 'postmerge') {
            stageWithWip(name: "postmerge", wip: 4) {
                    runScope('postmerge')
            }
            parallel (
                postpostmerge: {
                    stageWithWip(name: "post-post-merge", wip: 2) {
                        runScope('post-post-merge')
                    }
                },
                paralleltopostpostmerge: {
                    stage('serial stage a') {
                        echo "Just to show that fan out works as well"
                    }
                    stage('serial stage b') {
                        echo "yeah, you can serial steps here, or fan out again"
                    }
                },
                failFast: false
            )
            stage("post-post-post-merge") {
                echo "And fan in - but don't do that, its annoying!"
            }
        }
        if (params.SCOPE == 'check') {
            runScope(params.SCOPE)
        }
    }
    // What if i want to do wacky combinations of stageWithWip and dependentGate?
    // - go right ahead, but i dont support dependentGate in dependentGate because i dont know what it would mean
}
